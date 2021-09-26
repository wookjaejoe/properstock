package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.repository.TickerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TickerUpdater {

    @Autowired
    lateinit var repository: TickerRepository

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    /**
     * 티커 목록 업데이트
     */
    @Test
    fun updateAll() {
        naverFinanceCrawler.crawlAllTickers()
            .subscribe {
                repository.findByCode(it.code).subscribe { oldTicker -> it.id = oldTicker.id }
                repository.save(it).block()
                println(it)
            }
    }

    @Test
    fun findAll() {
        repository.findAll().subscribe { println(it) }
    }
}