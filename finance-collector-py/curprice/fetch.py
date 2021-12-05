import logging
from dataclasses import dataclass
from datetime import date, timedelta
from datetime import datetime, timezone
from typing import *

from pykrx import stock as krx
from retrying import retry

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
    price: int
    updated_at: datetime


class KrxCurrentPriceFetcher:
    """
    한국거래소를 통해 현재가를 구하여 반환. 현재가 외에도 시가, 등락율 등 데이터 포함하고 있음.
    """

    @retry(wait_exponential_multiplier=2_000, wait_exponential_max=64_000)
    def fetch(self) -> List[KrxStockCurrentInfo]:
        try:
            ref_date_str = date_str(nearest_business_date())
            prices_by_code = krx.get_market_price_change_by_ticker(
                fromdate=ref_date_str,
                todate=ref_date_str,
                market='ALL'
            )['종가'].to_dict()
            now = datetime.utcnow()
            return [KrxStockCurrentInfo(code, price, now) for code, price in prices_by_code.items()]
        except BaseException as e:
            logger.warning(f'An error occurs while fetching current price from krx: {e}')
            raise e
