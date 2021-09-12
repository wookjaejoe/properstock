import logging

import numpy as np
import pandas as pd
import requests
import yfinance as yf

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def crawl():
    headers = ['순위', '기업명&티커', '시가총액', '국가']

    # 페이지 내 데이터 크롤링
    html = requests.get('https://sichongnet.tistory.com/1?category=932118').text
    contents = pd.read_html(html)
    content: pd.DataFrame = contents[0]

    # 첫행, 둘째 행은 불필요해서 삭제
    content = content.drop(labels=[0, 1])

    # 마샬링
    def marshal(arg: str):
        return arg.split(' ')[-1]

    content[1] = content[1].apply(marshal)
    content.index = content[1]

    df = pd.DataFrame()
    symbols = content.index
    for symbol in symbols:
        logger.info(f'[{list(content.index).index(symbol) + 1}/{len(list(content.index))}] {symbol}')
        ticker = yf.Ticker(symbol)
        info: dict = ticker.info
        price_history: pd.DataFrame = ticker.history(period='max', interval='3mo', actions=False)
        close = price_history['Close']
        close = close[[i for i in close.index if i.day == 1 and i.month in [3, 6, 9, 12]]]
        currency = info.get('currency')
        if currency != 'USD':
            # USD로 변환
            exchange_symbol = f'{currency}USD=X'
            exchange_ticker = yf.Ticker(exchange_symbol)
            exchange_history = exchange_ticker.history(period='max', interval='3mo', actions=False)
            for i in close.index:
                try:
                    close[i] = close[i] * exchange_history['Close'][i]
                except BaseException as e:
                    close[i] = np.NaN
                    logger.warning(str(e))

        shares_outstanding = info.get('sharesOutstanding')
        market_cap_history = close * shares_outstanding

        basic = {
            'symbol': symbol,
            'name': info.get('shortName'),
            'country': info.get('country'),
        }

        basic.update(market_cap_history.to_dict())
        new_df = pd.DataFrame(index=[symbol], columns=basic.keys(), data=basic)
        df = pd.concat([df, new_df])

    whitelist = set()
    for col in df.columns[3:]:
        top_15 = df.sort_values(by=[col], ascending=False).index[:15]
        for w in top_15:
            whitelist.add(w)

    df = df.loc[df.index.isin(whitelist)]
    df.columns = df.columns.tolist()[:3] + [t.strftime('%b') + ' ' + t.strftime('%Y') for t in
                                            sorted(df.columns.tolist()[3:])]
    df.to_csv('top100.csv')


def main():
    crawl()


if __name__ == '__main__':
    main()
