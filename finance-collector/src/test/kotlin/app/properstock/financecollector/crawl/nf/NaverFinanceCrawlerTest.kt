package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.service.WebBrowseDriverManager
import org.junit.jupiter.api.Test

internal class NaverFinanceCrawlerTest {

    private val nf = NaverFinanceCrawler(WebBrowseDriverManager())

    @Test
    fun testCrawlTickers() {
        nf.crawlAllTickers().subscribe { println(it) }
    }

    @Test
    fun testCrawlCompany() {
        println(nf.crawlFinancialAnalysis("005930"))
    }
}