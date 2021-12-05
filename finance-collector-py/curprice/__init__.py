from threading import Thread

import jsons

from .fetch import *
from .server import *
import time


class CurrentPricePublisher:
    def __init__(self):
        self.fetcher = KrxCurrentPriceFetcher()
        self.server = WebsocketServer(9090)

    def __start_publish(self):
        prev_infos: Dict[str, KrxStockCurrentInfo] = {}

        while True:
            cur_infos = self.fetcher.fetch()
            logger.info(f'{len(cur_infos)} prices fetched.')
            updated_infos: Dict[str, KrxStockCurrentInfo] = {}
            for cur_info in cur_infos:
                updated = False
                if cur_info.code in prev_infos:
                    if prev_infos[cur_info.code].price != cur_info.price:
                        # 이전 현재가 정보가 있는데, 값이 다름. 업데이트 대상
                        updated = True
                else:
                    # 이전 현재가 정보 없음. 업데이트 대상
                    updated = True

                if updated:
                    updated_infos.update({cur_info.code: cur_info})

            if updated_infos:
                logger.info(f'Publishing {len(updated_infos)} updated current price...')
                msg = jsons.dumps(updated_infos.values(), jdkwargs={'indent': 2})
                self.server.publish(msg)
                prev_infos.update(updated_infos)
            else:
                logger.info(f'Nothing to update.')

            time.sleep(3)

    def start(self):
        Thread(target=self.__start_publish).start()
        self.server.start()
