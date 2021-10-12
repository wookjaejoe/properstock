package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.model.FinanceAnalysis
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.TickerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

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
        val tickers = tickerRepository.findAll().collectList().block()!!
        var success = 0
        var fail = 0
        for (ticker in tickers) {
            try {
                val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(ticker.code)
                financeAnalysisRepository.save(financeAnalysis).block()
                success++
                println("Success: ${ticker.name} - ${ticker.code}")
            } catch (e: Throwable) {
                fail++
                println("Fail: ${ticker.name} - ${ticker.code} caused by ${e.message}")
            }
        }

        println("Success: $success")
        println("Fail: $fail")
    }
}