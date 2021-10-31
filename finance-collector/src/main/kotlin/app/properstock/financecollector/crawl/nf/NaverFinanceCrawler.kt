package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.crawl.WebDriverConnector
import app.properstock.financecollector.model.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.openqa.selenium.By
import org.openqa.selenium.interactions.Actions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.YearMonth
import java.util.stream.Stream
import kotlin.streams.asStream

const val INNER_HTML = "innerHTML"
const val OUTER_HTML = "outerHTML"

fun String.convertToDouble(): Double? = try {
    this.trim().replace(",", "").toDouble()
} catch (e: Throwable) {
    null
}

fun String.parseDouble(): Double? = try {
    "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?".toRegex().find(this)?.value?.convertToDouble()
} catch (e: Throwable) {
    null
}

@Component
class NaverFinanceCrawler(
    val webDriverConnector: WebDriverConnector
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(NaverFinanceCrawler::class.java)
    }

    /**
     * 네이버 파이낸스에서 크롤링 가능한 모든 티커 목록을 크롤링하여 반환
     */
    fun crawlAllTickers(): Stream<Ticker> {
        logger.info("Crawling all tickers started.")
        return sequence {
            for (ticker in crawlTickers(Market.KOSPI)) yield(ticker)
            for (ticker in crawlTickers(Market.KOSDAQ)) yield(ticker)
        }.asStream()
    }

    /**
     * 특정 시장의 모든 티커 목록 크롤링하여 반환
     */
    fun crawlTickers(market: Market): Stream<Ticker> {
        logger.info("Crawling ticker in ${market.name} started.")
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
        logger.info("Crawling ticker in ${market.name}:$page started.")
        val url = NaverFinanceUrls.tickers(market, page)
        return webDriverConnector.connect {
            get(url)
            // 테이블 탐색
            val tableHtml = findElements(By.tagName("table"))
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
            table.getElementsByTag("tr")
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
        }
    }

    fun crawlFinancialAnalysis(code: String): FinanceAnalysis {
        logger.info("Crawling financial analysis for $code started.")
        val url = NaverFinanceUrls.companyInfo(code)
        return webDriverConnector.connect {
            val actions = Actions(this)
            get(url)
            // 연간 탭 클릭
            val yearlyTab = findElement(By.id("cns_Tab21"))
            actions.click(yearlyTab).build().perform()

            val tableHtml = findElements(By.tagName("table")).find {
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
                    financeSummary.sales.set(headers, this["sales"]!!.map { it?.convertToDouble()?.times(1_0000_0000) })
                    financeSummary.operatingProfit.set(
                        headers,
                        this["operatingProfit"]!!.map { it?.convertToDouble()?.times(1_0000_0000) })
                    financeSummary.netProfit.set(
                        headers,
                        this["netProfit"]!!.map { it?.convertToDouble()?.times(1_0000_0000) })
                    financeSummary.roe.set(headers, this["roe"]!!.map { it?.convertToDouble() })
                    financeSummary.eps.set(headers, this["eps"]!!.map { it?.convertToDouble() })
                    financeSummary.per.set(headers, this["per"]!!.map { it?.convertToDouble() })
                    financeSummary.issuedCommonShares.set(
                        headers,
                        this["issuedCommonShares"]!!.map { it?.convertToDouble() })
                }

            FinanceAnalysis(
                code = code,
                financeSummary = financeSummary
            )
        }
    }

    fun crawlIndustries(): Stream<NaverFinanceIndustry> {
        val industries: List<NaverFinanceIndustry> = webDriverConnector.connect {
            // 업종 페이지 입장
            get(NaverFinanceUrls.industries())

            // 테이블 파싱
            val trList: Elements = findElement(By.tagName("table"))
                .getAttribute(OUTER_HTML)
                .run {
                    Jsoup.parse(this).getElementsByTag("tr")
                }

            trList
                .subList(2, trList.size)
                .filter { it.text().trim().isNotEmpty() }
                .map { row ->
                    val tdList = row.getElementsByTag("td")
                    val aTag = tdList[0].getElementsByTag("a")
                    val ref = aTag.attr("href")
                    val name = aTag.text().trim()
                    val marginRate = tdList[1].text().parseDouble()
                    NaverFinanceIndustry(
                        name = name,
                        ref = ref,
                        marginRate = marginRate,
                    )
                }
        }

        return sequence {
            industries.forEach {
                yield(webDriverConnector.connect {
                    // 업종 상세 페이지 입장
                    get(NaverFinanceUrls.resolve(it.ref))
                    val table = findElements(By.tagName("table"))[2].getAttribute(OUTER_HTML)

                    it.tickerRefs = Jsoup.parse(table)
                        .getElementsByTag("tbody")[0]
                        .getElementsByTag("tr")
                        .mapNotNull { val aList = it.getElementsByTag("a"); if (aList.size > 0) aList[0].attr("href") else null }
                    it
                })
            }
        }.asStream()
    }
}