package app.properstock.crawl.nf

import app.properstock.model.Market
import org.junit.jupiter.api.Test
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

internal class NaverFinanceCrawlerTest {
    private val driver: ChromeDriver

    init {
        System.setProperty(
            "webdriver.chrome.driver",
            """/Users/wjjo/chromedriver-93-0-4577-63"""
        )
        val chromeOptions = ChromeOptions()
        chromeOptions.setHeadless(true)
        driver = ChromeDriver(chromeOptions)
    }

    @Test
    fun testCrawlTickers() {
        val crawler = NaverFinanceCrawler(driver)
        crawler.crawlTickers(Market.KOSPI, 1)
    }

    @Test
    fun testCrawlFinance() {
        val crawler = NaverFinanceCrawler(driver)
        crawler.crawlCoInfo("005930")
    }
}
