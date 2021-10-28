from curprice import KrxCurrentPricePublisher
import log
import os

log.init()


def main():
    publisher = KrxCurrentPricePublisher(
        rmq_host=os.getenv('RMQ_HOST', default='home.jowookjae.in'),
        rmq_port=os.getenv('RMQ_PORT', default=5672),
    )

    try:
        publisher.start()
    finally:
        publisher.close()


if __name__ == '__main__':
    main()
