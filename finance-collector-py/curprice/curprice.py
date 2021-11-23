import logging
import time
from dataclasses import dataclass
from datetime import date, timedelta
from datetime import datetime, timezone, tzinfo
from typing import *

import jsons
import pika
from pykrx import stock as krx


logger = logging.getLogger(__name__)


def date_str(d: date):
    """
    날짜를 문자열로 변환하여 반환 - yyyyMMdd
    :param d:
    :return:
    """
    return d.strftime('%Y%m%d')


def nearest_business_date() -> date:
    """
    가까운 영업일을 구해서 반환
    """
    curr = date_str(date.today())
    prev = date_str(date.today() - timedelta(days=30))
    df = krx.get_index_ohlcv_by_date(
        prev,
        curr,
        "1001"
    )

    return df.index[-1]


@dataclass
class KrxStockCurrentInfo:
    code: str
    price: float
    updated_at: datetime


class KrxCurrentPriceFetcher:
    """
    한국거래소를 통해 현재가를 구하여 반환. 현재가 외에도 시가, 등락율 등 데이터 포함하고 있음.
    """

    def __init__(self):
        self.ref_date_str = date_str(nearest_business_date())

    def fetch(self) -> List[KrxStockCurrentInfo]:
        prices_by_code = krx.get_market_price_change_by_ticker(
            fromdate=self.ref_date_str,
            todate=self.ref_date_str,
            market='ALL'
        )['종가'].to_dict()
        now = datetime.now(timezone.utc)
        return [KrxStockCurrentInfo(code, price, now) for code, price in prices_by_code.items()]


class KrxCurrentPricePublisher:
    def __init__(
            self,
            rmq_host: str,
            rmq_port: int,
            exchange: str = 'currentPrice'
    ):
        self.exchange = exchange
        self.fetcher = KrxCurrentPriceFetcher()
        logger.info(f'Connecting to {rmq_host}:{rmq_port}...')
        self.rmq_conn = pika.BlockingConnection(
            pika.ConnectionParameters(host=rmq_host, port=rmq_port, socket_timeout=3600)
        )

    def start(self):
        logger.info(f'Staring to publish krx current price into {self.exchange}@exchange...')
        channel = self.rmq_conn.channel()
        channel.exchange_declare(exchange=self.exchange, exchange_type='fanout', durable=True)

        prev_infos: Dict[str, KrxStockCurrentInfo] = {}
        while True:
            cur_infos = self.fetcher.fetch()
            logger.info(f'{len(cur_infos)} prices fetched.')
            updated_infos = {}
            for cur_info in cur_infos:
                updated = False
                if cur_info.code in prev_infos:
                    if prev_infos[cur_info.code].price != cur_info.price:
                        # 이전 현재가 정보가 있는데, 값이 다름. 업데이트 대상
                        updated = True

                else:
                    # 이전 현재가 정보 없음. 업데이트 대상
                    updated = True

                if updated:
                    updated_infos.update({cur_info.code: cur_info})

            if updated_infos:
                logger.info(f'Publishing {len(updated_infos)} updated current price...')
                channel.basic_publish(
                    exchange=self.exchange,
                    routing_key='',
                    body=jsons.dumps(updated_infos.values()).encode('utf-8')
                )

                prev_infos.update(updated_infos)

                # 너무 잦은 요청 시 API에서 값을 안주는거 같아서, 요청 속도 제어
                time.sleep(5)
            else:
                deplay = 60
                logger.info(f'Updated count is 0. Sleeping {deplay} seconds...')
                time.sleep(deplay)

    def close(self):
        self.rmq_conn.close()
