import logging

import pandas as pd
import requests
import yfinance as yf

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def remove_peek(df: pd.DataFrame, column_name: str):
    return df[column_name].resample('1W', how='mean')


def crawl():
    headers = ['순위', '기업명&티커', '시가총액', '국가']

    # 페이지 내 데이터 크롤링
    html = requests.get('https://sichongnet.tistory.com/4?category=932341').text
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
    # symbols = ['000270.KS']
    for symbol in [s for s in symbols if s not in ['NAS.OL']]:
        logger.info(f'[{list(content.index).index(symbol) + 1}/{len(list(content.index))}] {symbol}')
        ticker = yf.Ticker(symbol)
        info: dict = ticker.info
        price_history: pd.DataFrame = ticker.history(period='max', interval='1mo', actions=False)
        close = price_history['Close']
        close = close[[i for i in close.index if i.day == 1 and i.month in [3, 6, 9, 12]]]
        currency = info.get('currency')
        logger.info(f'currency: {currency}')
        if currency != 'USD':
            # USD로 변환
            exchange_symbol = f'{currency}USD=X'
            exchange_ticker = yf.Ticker(exchange_symbol)
            exchange_history = exchange_ticker.history(period='max', interval='1mo', actions=False)

            # 노이즈 필터링
            exchange_history['Close'] = exchange_history['Close'].rolling(10, center=True, min_periods=1).median()
            for i in close.index:
                try:
                    ex = exchange_history['Close'][i]
                except:
                    ex = exchange_history['Close'][exchange_history.index[0]]

                logger.info(f'{i} {close[i]} * {ex}')
                close[i] = close[i] * ex
                logger.info(f'= {close[i]}')

        current_price = info.get('currentPrice')
        market_cap = info.get('marketCap')
        if not market_cap:
            continue

        shares = market_cap / current_price
        market_cap_history = close * shares
        market_cap_history.round()

        basic = {
            'symbol': symbol,
            'name': info.get('shortName'),
            'country': info.get('country'),
        }

        basic.update(market_cap_history.to_dict())
        new_df = pd.DataFrame(basic, index=[symbol])
        df = pd.concat([df, new_df])

    whitelist = set()
    for col in df.columns[3:]:
        top_15 = df.sort_values(by=[col], ascending=False).index[:15]
        for w in top_15:
            whitelist.add(w)

    df = df.loc[df.index.isin(whitelist)]
    sorted_columns = df.columns.to_list()[:3] + sorted(df.columns.to_list()[3:])
    df = df[sorted_columns]
    df.columns = df.columns.to_list()[:3] + [t.strftime('%b') + ' ' + t.strftime('%Y') for t in
                                             df.columns.to_list()[3:]]
    df.to_csv('top100.csv')


def main():
    crawl()


if __name__ == '__main__':
    main()
