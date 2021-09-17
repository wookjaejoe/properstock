from nf import NaverFinanceClient, KrMarket


def main():
    print(NaverFinanceClient.tickers(KrMarket.KOSPI, page=1))


if __name__ == '__main__':
    main()