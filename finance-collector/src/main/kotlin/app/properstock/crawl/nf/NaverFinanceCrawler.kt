package app.properstock.crawl.nf

import app.properstock.model.Market
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions

class NaverFinanceCrawler(
    private val driver: ChromeDriver
) {
    private val actions = Actions(driver)
    private val urls = NaverFinanceUrls()

    fun crawlTickers(market: Market, page: Int = 1) {
        val url = urls.tickers(market, page)
        println(url)
        driver.get(url)
        println(driver.findElement(By.tagName("html")).getAttribute("outerHTML"))
    }

    fun crawlCoInfo(code: String) {
        // 네이버 파이낸스 접속
        driver.get(urls.coInfo(code))

        // 종목분석 소스 접속
        val coInfoUrl = driver.findElement(By.id("coinfo_cp")).getAttribute("src")
        driver.get(coInfoUrl)

        // 분기 탭 클릭
        val quarterTap = driver.findElement(By.id("cns_Tab22"))
        actions.click(quarterTap).build().perform()

        val finance = driver.findElements(By.tagName("table")).find {
            try {
                it.findElement(By.tagName("caption")).getAttribute("innerHTML") == "주요재무정보"
            } catch (e: Throwable) {
                false
            }
        }

        println(finance!!.getAttribute("outerHTML"))
    }
}