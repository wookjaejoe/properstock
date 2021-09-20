package app.properstock.crawl.nf

import app.properstock.crawl.HtmlTableReader
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
        var page = 1

        while (true) {
            val tickers = crawler.crawlTickers(Market.KOSPI, page)
            if (tickers.isEmpty()) {
                break
            }

            println(tickers)
            page++
        }
    }

    @Test
    fun testCrawlFinance() {
        val crawler = NaverFinanceCrawler(driver)
        crawler.crawlCoInfo("005930")
    }
}
