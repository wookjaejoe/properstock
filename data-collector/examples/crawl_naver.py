import pandas as pd
import requests

samsung_code = '005930'


def get_url(aspect: str, code: str):
    return f'https://finance.naver.com/item/{aspect}.nhn?code={code}'


def crawl(code: str):
    html = requests.get('https://navercomp.wisereport.co.kr/v2/company/ajax/cF1001.aspx?cmp_cd=005930&fin_typ=0&freq_typ=Y&encparam=Sm92dHk5eVlzRTdGRkRiZ3ArS0g4QT09&id=aFVlanREZS').text
    print(html)

    # contents = pd.read_html(html)
    # for content in contents:
    #     print(content)
    #     print()


def main():
    crawl(samsung_code)


if __name__ == '__main__':
    main()
