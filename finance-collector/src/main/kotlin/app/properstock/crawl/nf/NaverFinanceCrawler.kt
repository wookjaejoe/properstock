package app.properstock.crawl.nf

import app.properstock.crawl.HtmlTable
import app.properstock.crawl.HtmlTableReader
import app.properstock.crawl.nf.model.Ticker
import app.properstock.model.Market
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions

const val INNER_HTML = "innerHTML"
const val OUTER_HTML = "outerHTML"

class NaverFinanceCrawler(
    private val driver: ChromeDriver
) {
    private val actions = Actions(driver)
    private val urls = NaverFinanceUrls()

    fun crawlTickers(market: Market, page: Int = 1): List<Ticker> {
        val url = urls.tickers(market, page)

        // 페이지 접속
        driver.get(url)

        // 테이블 탐색
        val htmlTable = driver.findElements(By.tagName("table"))
            .find {
                try {
                    it.findElement(By.tagName("caption")).getAttribute(INNER_HTML) == "코스피"
                } catch (e: Throwable) {
                    false
                }
            }!!
            .getAttribute(OUTER_HTML)

        val table = HtmlTableReader(htmlTable).read()
        return (0 until table.rows.size)
            .map { index ->
                Ticker(
                    name = table.get(index, "종목명"),
                    price = table.get(index, "현재가").replace(",", "").toInt(),
                    marketCap = table.get(index, "시가총액").replace(",", "").toLong(),
                    shares = table.get(index, "상장주식수").replace(",", "").toInt()
                )
            }
    }

    fun crawlCoInfo(code: String): String {
        // 네이버 파이낸스 접속
        driver.get(urls.coInfo(code))

        // 종목분석 소스 접속
        val coInfoUrl = driver.findElement(By.id("coinfo_cp")).getAttribute("src")
        driver.get(coInfoUrl)

        // 분기 탭 클릭
        val quarterTap = driver.findElement(By.id("cns_Tab22"))
        actions.click(quarterTap).build().perform()

        val html = driver.findElements(By.tagName("table")).find {
            try {
                it.findElement(By.tagName("caption")).getAttribute(INNER_HTML) == "주요재무정보"
            } catch (e: Throwable) {
                false
            }
        }

        return html!!.getAttribute(OUTER_HTML)
    }
}