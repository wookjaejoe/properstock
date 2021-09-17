import app.properstock.crawl.nf.NaverFinanceCrawler
import app.properstock.model.Market
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

fun main() {
    System.setProperty(
        "webdriver.chrome.driver",
        """/Users/wjjo/chromedriver-93-0-4577-63"""
    )

    val chromeOptions = ChromeOptions()
    chromeOptions.setHeadless(true)

    val driver = ChromeDriver(chromeOptions)
    try {
        NaverFinanceCrawler(driver).crawlTickers(Market.KOSPI)
        NaverFinanceCrawler(driver).crawlCoInfo("005930")
    } finally {
        driver.close()
    }

}