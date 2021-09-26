package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.TickerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FinanceSummaryUpdater {

    @Autowired
    lateinit var financeAnalysisRepository: FinanceAnalysisRepository

    @Autowired
    lateinit var tickerRepository: TickerRepository

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    @Test
    fun updateAll() {
        // fixme: 이거 왜 리액티브로 안도냐... 미치겄네
        tickerRepository.findAll().collectList().block()!!
            .forEach {
                try {
                    val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(it.code)
                    financeAnalysisRepository.save(financeAnalysis).block()
                } catch (e: Throwable) {
                    println("An error occurs while crawling ${it.code}:${it.name}")
                    println(e.message ?: "No error message")
                }
            }
    }
}