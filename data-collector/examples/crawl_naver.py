import pandas as pd
import requests

samsung_code = '005930'


def get_url(aspect: str, code: str):
    return f'https://finance.naver.com/item/{aspect}.nhn?code={code}'


def crawl(code: str):
    # url = get_url('main', code)
    url = get_url('coinfo', code)
    print(url)
    html = requests.get(url).text
    contents = pd.read_html(html)
    for content in contents:
        print(content)
        print()


def main():
    crawl(samsung_code)


if __name__ == '__main__':
    main()
