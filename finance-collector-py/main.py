from curprice import CurrentPricePublisher
import log

log.init()

VERSION = '0.2.0-SNAPSHOT'


def main():
    CurrentPricePublisher().start()


if __name__ == '__main__':
    main()
