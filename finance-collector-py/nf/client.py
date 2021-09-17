import enum
import requests
import pandas as pd


class KrMarket(enum.Enum):
    KOSPI = enum.auto(),
    KOSDAQ = enum.auto()


class NaverFinanceClient:
    @classmethod
    def tickers(cls, market: KrMarket, page=-1):
        if market == KrMarket.KOSPI:
            sosok = 0
        elif market == KrMarket.KOSDAQ:
            sosok = 1
        else:
            raise ValueError(f'Not supported market: {market.name}')

        url = f'https://finance.naver.com/sise/sise_market_sum.nhn?sosok={sosok}&page={page}'
        html = requests.get(url).text
        print(pd.read_html(html))