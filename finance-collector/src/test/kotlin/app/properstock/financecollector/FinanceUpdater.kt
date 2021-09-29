package app.properstock.financecollector

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.TickerRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FinanceUpdater {

    @Autowired
    lateinit var financeAnalysisRepository: FinanceAnalysisRepository

    @Autowired
    lateinit var tickerRepository: TickerRepository

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    @Test
    fun updateAll() {
        val tickers = tickerRepository.findAll().collectList().block()
        var success = 0
        var fail = 0
        tickers?.forEach {
            try {
                val financeAnalysis = naverFinanceCrawler.crawlFinancialAnalysis(it.code)
                val oldValue = financeAnalysisRepository.findByCode(financeAnalysis.code).block()
                if(oldValue != null) {
                    financeAnalysis.id = oldValue.id
                }

                println(financeAnalysisRepository.save(financeAnalysis).block())
                success++
            } catch (e: Throwable) {
                println("An error occurs(${it.name}): ${e.message}")
                fail++
            }
        }

        println("Success: $success")
        println("Fail: $fail")
    }
}