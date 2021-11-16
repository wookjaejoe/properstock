package app.properstock.financecollector.service.proper

import app.properstock.financecollector.crawl.nf.NaverFinanceCrawler
import app.properstock.financecollector.service.proper.formula.SmartInvestor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class ProperPriceServiceTest {

    @Autowired
    lateinit var properPriceService: ProperPriceService

    @Autowired
    lateinit var smartInvestor: SmartInvestor

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    @Test
    fun calcSmartInvestor() {
        val x = naverFinanceCrawler.crawlCorpStat("005930")
//        val x = smartInvestor.calculate("005930")
        println()
    }

    @Test
    fun calculate() {
        println(properPriceService.calculate("005930"))
    }

    @Test
    fun updateAll() {
        properPriceService.updateAll()
    }

    @Test
    fun update() {
        properPriceService.update("005930")
    }
}