import csv
import logging
from multiprocessing.pool import ThreadPool

import finnhub
import pandas
import yfinance as yf
from pymongo import MongoClient
from retrying import retry

from examples.exchanges import get_all_exchanges

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

finnhub_client = finnhub.Client(api_key="c4prhm2ad3icgcuepdm0")
mongodb_client = MongoClient('localhost', 27017)
finance_db = mongodb_client['finance']
common_stocks_col = finance_db['commonStocks']


class Storage:
    filename = 'just-symbols.txt'

    def __init__(self):
        self.symbols = []

    @classmethod
    def load(cls):
        instance = Storage()
        with open(cls.filename) as f:
            instance.symbols = [line for line in f.read().split() if line]
        return instance

    def save(self):
        with open(self.filename, 'w') as f:
            f.write('\n'.join(self.symbols))

    def fetch(self, exchanges: list = None):
        if exchanges is None:
            exchanges = get_all_exchanges()

        @retry(stop_max_attempt_number=7, wait_fixed=10_000)
        def _get_symbols(_exchange: str):
            return finnhub_client.stock_symbols(_exchange)

        for exchange in exchanges:
            try:
                df = pandas.DataFrame(_get_symbols(exchange))
                df = df.loc[df['type'] == 'Common Stock']
                symbols = df['symbol'].values
                self.symbols.extend(symbols)
            except Exception as e:
                logger.error(e)


def main():
    try:
        storage = Storage.load()
    except:
        storage = Storage()
        storage.fetch()
        storage.save()

    all_symbols = storage.symbols

    ks_symbols = [symbol for symbol in all_symbols if symbol.endswith('.KS')]
    logger.info(f'KS symbols = : {len(ks_symbols)}', )
    with open('symbols.csv', 'a', newline='') as f:

        # for symbol in all_symbols:
        @retry(stop_max_attempt_number=3, wait_fixed=60_000)
        def fetch_and_insert(symbol: str):
            if common_stocks_col.find_one({'_id': symbol}):
                # already exists
                return

            info = yf.Ticker(symbol).info
            if 'symbol' not in info:
                return

            logger.info(f'[{ks_symbols.index(symbol)}/{len(all_symbols)}]')
            common_stocks_col.insert({'_id': symbol, **info})

        pool = ThreadPool(16)
        pool.map(fetch_and_insert, ks_symbols)


if __name__ == '__main__':
    main()
