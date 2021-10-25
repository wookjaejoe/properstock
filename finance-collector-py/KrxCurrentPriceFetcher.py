from pykrx import stock as krx
from datetime import date, timedelta
import pandas as pd
import pika
from datetime import datetime
import json
from typing import *
from dataclasses import dataclass


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
class KrxCurrentPrice:
    code: str
    price: float
    updated: datetime


class KrxCurrentPriceFetcher:
    """
    한국거래소를 통해 현재가를 구하여 반환. 현재가 외에도 시가, 등락율 등 데이터 포함하고 있음.
    """

    def __init__(self):
        self.ref_date_str = date_str(nearest_business_date())

    def fetch(self) -> List[KrxCurrentPrice]:
        prices_by_code = krx.get_market_price_change_by_ticker(
            fromdate=self.ref_date_str,
            todate=self.ref_date_str,
            market='ALL'
        )['종가'].to_dict()
        now = datetime.now()
        return [KrxCurrentPrice(code, price, now) for code, price in prices_by_code]


class KrxCurrentPricePublisher:
    def __init__(self):
        self.fetcher = KrxCurrentPriceFetcher()
        self.rmq_conn = pika.BlockingConnection(
            pika.ConnectionParameters(host='localhost', port=5672)
        )

    def start(self):
        channel = self.rmq_conn.channel()
        channel.exchange_declare(exchange='logs', exchange_type='fanout')

        # 여기서 전 값이랑 비교
        while True:
            self.fetcher.fetch()
            # todo: publish message
            channel.basic_publish(exchange='logs', routing_key='', body="Not implemented yet")

    def close(self):
        self.rmq_conn.close()


def main():
    publisher = KrxCurrentPricePublisher()
    publisher.start()
    publisher.close()


if __name__ == '__main__':
    main()
