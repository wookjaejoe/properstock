package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.Market
import app.properstock.financecollector.model.Ticker
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.DbSeqGenerator
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
    lateinit var dbSeqGenerator: DbSeqGenerator

    @Test
    fun testCrawlTickers() {
        naverFinanceCrawler.crawlAllTickers().subscribe { println(it) }
    }

    /**
     * 티커 목록 업데이트
     */
    @Test
    fun updateTickers() {
        naverFinanceCrawler.crawlAllTickers()
            .subscribe {
                val oldTicker = repository.findByCode(it.code).block()
                it.id = oldTicker?.id ?: dbSeqGenerator.generate(Ticker.seqName).block()!!.value
                repository.save(it).block()
                println(it)
            }
    }

    @Test
    fun findAll() {
        repository.findAll().all { println(it); true }  // fixme
    }
}