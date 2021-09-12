import pandas as pd
import requests

def crawl():
    html = requests.get('https://sichongnet.tistory.com/1?category=932118').text
    contents = pd.read_html(html)
    for content in contents:
        print(content)
        print()


def main():
    crawl()


if __name__ == '__main__':
    main()
