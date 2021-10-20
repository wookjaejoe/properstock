package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.model.*
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.YearMonth
import java.util.stream.Stream
import kotlin.streams.asStream

const val INNER_HTML = "innerHTML"
const val OUTER_HTML = "outerHTML"

fun String.convertToLong(): Long = this.trim().replace(",", "").toLong()
fun String.convertToDouble(): Double? = try {
    this.trim().replace(",", "").toDouble()
} catch (e: Throwable) {
    null
}

@Component
class NaverFinanceCrawler(
    val webDriver: WebDriver,
) {
    /**
     * 네이버 파이낸스에서 크롤링 가능한 모든 티커 목록을 크롤링하여 반환
     */
    fun crawlAllTickers(): Stream<Ticker> {
        return sequence {
            for (ticker in crawlTickers(Market.KOSPI)) yield(ticker)
            for (ticker in crawlTickers(Market.KOSDAQ)) yield(ticker)
        }.asStream()
    }

    /**
     * 특정 시장의 모든 티커 목록 크롤링하여 반환
     */
    fun crawlTickers(market: Market): Stream<Ticker> {
        var page = 1
        return sequence {
            while (true) {
                val tickers = crawlTickers(market, page++)
                if (tickers.isEmpty()) break
                else yieldAll(tickers)
            }
        }.asStream()
    }

    /**
     * 특정 시장, 특정 페이지 모든 티커 크롤링하여 반환
     */
    fun crawlTickers(market: Market, page: Int): List<Ticker> {
        val url = NaverFinanceUrls.tickers(market, page)
        try {
            webDriver.get(url)
            // 테이블 탐색
            val tableHtml = webDriver.findElements(By.tagName("table"))
                .find {
                    try {
                        listOf("코스피", "코스닥").contains(it.findElement(By.tagName("caption")).getAttribute(INNER_HTML))
                    } catch (e: Throwable) {
                        false
                    }
                }!!
                .getAttribute("outerHTML")

            // 크롬 크롤링 결과에서 바로 탐색할 경우 성능 이슈 있기 때문에 Jsoup 사용
            val table = Jsoup.parse(tableHtml)
            val headers = table.getElementsByTag("th").map { it.text() }
            return table.getElementsByTag("tr")
                .filter { it.text().isNotBlank() }
                .map { it.getElementsByTag("td") }
                .filter { !it.isNullOrEmpty() }
                .map {
                    val link = it[headers.indexOf("종목명")].getElementsByTag("a")[0].attr("href")
                    Ticker(
                        market = market,
                        code = "(?<=code=)[A-Za-z0-9]+".toRegex().find(link)!!.value,
                        name = it[headers.indexOf("종목명")].text().replace(",", ""),
                        price = it[headers.indexOf("현재가")].text().replace(",", "").toInt(),
                        marketCap = it[headers.indexOf("시가총액")].text().replace(",", "").toLong() * 1_0000_0000,
                        shares = it[headers.indexOf("상장주식수")].text().replace(",", "").toInt() * 1000,
                        link = NaverFinanceUrls.root + link,
                        updated = Instant.now()
                    )
                }
        } catch (e: Throwable) {
            throw RuntimeException("Failed to crawling $url", e)
        }
    }

    fun crawlFinancialAnalysis(code: String): FinanceAnalysis {
        val url = NaverFinanceUrls.companyInfo(code)
        val actions = Actions(webDriver)
        try {
            webDriver.get(url)
            // 연간 탭 클릭
            val yearlyTab = webDriver.findElement(By.id("cns_Tab21"))
            actions.click(yearlyTab).build().perform()

            val tableHtml = webDriver.findElements(By.tagName("table")).find {
                try {
                    it.findElement(By.tagName("caption")).getAttribute(INNER_HTML) == "주요재무정보"
                } catch (e: Throwable) {
                    false
                }
            }!!.getAttribute(OUTER_HTML)

            val table = Jsoup.parse(tableHtml)
            val headers = table
                .getElementsByTag("thead")[0]
                .getElementsByTag("tr")[1]
                .getElementsByTag("th")
                .map {
                    val str = "[0-9]{4}/[0-9]{2}".toRegex().find(it.text())?.value!!
                    val spl = str.split("/")
                    YearMonth.of(spl[0].toInt(), spl[1].toInt())
                }

            val financeSummary = FinanceSummary()

            table
                .getElementsByTag("tbody")[0]
                .getElementsByTag("tr").map { element ->
                    val title = element.getElementsByTag("th")[0].text()
                    val values = element.getElementsByTag("td").map { td ->
                        td.text()
                            .replace(",", "")
                            .ifEmpty { null }
                    }

                    Pair(title, values)
                }.associate {
                    Pair(FINANCE_SUMMARY_INDICES[it.first], it.second)
                }.run {
                    financeSummary.sales.set(headers, this["sales"]!!.map { it?.convertToLong()?.times(1_0000_0000) })
                    financeSummary.operatingProfit.set(
                        headers,
                        this["operatingProfit"]!!.map { it?.convertToLong()?.times(1_0000_0000) })
                    financeSummary.netProfit.set(
                        headers,
                        this["netProfit"]!!.map { it?.convertToLong()?.times(1_0000_0000) })
                    financeSummary.roe.set(headers, this["roe"]!!.map { it?.convertToDouble() })
                    financeSummary.eps.set(headers, this["eps"]!!.map { it?.convertToLong() })
                    financeSummary.per.set(headers, this["per"]!!.map { it?.convertToDouble() })
                    financeSummary.issuedCommonShares.set(
                        headers,
                        this["issuedCommonShares"]!!.map { it?.convertToLong() })
                }

            return FinanceAnalysis(
                code = code,
                financeSummary = financeSummary
            )
        } catch (e: Throwable) {
            throw RuntimeException("Failed to crawling $url", e)
        }
    }
}
