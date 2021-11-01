package app.properstock.financecollector.crawl.nf

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class NaverFinanceCrawlerTest {

    @Autowired
    lateinit var naverFinanceCrawler: NaverFinanceCrawler

    @Test
    fun crawlAllTickers() {
    }

    @Test
    fun crawlTickers() {
    }

    @Test
    fun testCrawlTickers() {
    }

    @Test
    fun crawlFinancialAnalysis() {
    }

    @Test
    fun crawlIndustries() {
        naverFinanceCrawler.crawlIndustries().forEach { println(it) }
    }

    @Test
    fun crawlThemes() {
        naverFinanceCrawler.crawlThemes().forEach { println(it) }
    }
}