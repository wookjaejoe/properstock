package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.Market
import app.properstock.financecollector.model.Ticker
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.DatabaseSequenceGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TickerUpdater {

    @Autowired
    lateinit var repository: TickerRepository

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    @Autowired
    lateinit var databaseSequenceGenerator: DatabaseSequenceGenerator

    @Test
    fun testCrawlTickers() {
        naverFinanceCrawler.crawlAllTickers().subscribe { println(it) }
    }

    /**
     * 티커 목록 업데이트
     */
    @Test
    fun updateTickers() {
        naverFinanceCrawler.crawlTickers(market = Market.KOSPI, page = 1)
            .flatMap { newTicker ->
                val ticker = repository.findByCode(newTicker.code).block()
                if (ticker == null) {
                    newTicker.id = databaseSequenceGenerator.increaseSequence(Ticker.seqName).block()!!.value
                } else {
                    newTicker.id = ticker.id
                }

                println(newTicker)
                repository.save(newTicker)
            }
            .subscribe()
    }

    @Test
    fun findAll() {
        repository.findAll().all { println(it); true }  // fixme
    }
}