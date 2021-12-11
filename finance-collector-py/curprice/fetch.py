import logging
from dataclasses import dataclass
from datetime import date
from datetime import datetime
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
    return datetime.strptime(krx.get_nearest_business_day_in_a_week(), '%Y%m%d').date()


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
            return [KrxStockCurrentInfo(code, price, datetime.utcnow()) for code, price in prices_by_code.items()]
        except BaseException as e:
            logger.warning(f'An error occurs while fetching current price from krx: {e}')
            raise e
