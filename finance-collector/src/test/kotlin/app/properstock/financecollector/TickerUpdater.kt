package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.Market
import app.properstock.financecollector.repository.TickerRepository
import org.junit.jupiter.api.Test
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@SpringBootTest
class TickerUpdater {

    @Autowired
    lateinit var driver: ChromeDriver

    @Autowired
    lateinit var repository: TickerRepository

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    @Test
    fun testCrawlTickers() {

        var page = 1

        while (true) {
            val tickers = naverFinanceCrawler.crawlTickers(Market.KOSPI, page)
            if (tickers.isEmpty()) {
                break
            }

            for (ticker in tickers) {
                // fixme: 중복 관련해서 제대로 동작안하는듯
                // fixme: 업데이트 하면 updated 필드 수정되게 해야함. 이거 좀 한곳에서 공통 로직으로 빼고 싶은데..
                repository.findByCode(ticker.code)
                    .switchIfEmpty(Mono.just(ticker))
                    .map {
                        ticker.code = it.code
                        Mono.just(ticker)
                    }
                    .block()
            }

            repository.saveAll(tickers).blockLast()
            page++
        }
    }

    @Test
    fun findAll() {
        repository.findAll().all { println(it); true }  // fixme
    }
}