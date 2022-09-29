package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.stream.Stream
import kotlin.streams.asStream

const val INNER_HTML = "innerHTML"
const val OUTER_HTML = "outerHTML"

fun String.parseDouble(): Double? = try {
    "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?".toRegex().find(this.trim().replace(",", ""))?.value?.toDouble()
} catch (e: Throwable) {
    null
}

@Component
class NaverFinanceCrawler {
    private val logger: Logger = LoggerFactory.getLogger(NaverFinanceCrawler::class.java)

    /**
     * 네이버 파이낸스에서 크롤링 가능한 모든 티커 목록을 크롤링하여 반환
     */
    fun crawlAllTickers(webDriver: WebDriver): Stream<Ticker> {
        logger.debug("Crawling all tickers started.")
        return sequence {
            for (ticker in crawlTickers(webDriver, Market.KOSPI)) yield(ticker)
            for (ticker in crawlTickers(webDriver, Market.KOSDAQ)) yield(ticker)
        }.asStream()
    }

    /**
     * 특정 시장의 모든 티커 목록 크롤링하여 반환
     */
    fun crawlTickers(webDriver: WebDriver, market: Market): Stream<Ticker> {
        logger.debug("Crawling ticker in ${market.name} started.")
        var page = 1
        return sequence {
            while (true) {
                val tickers = crawlTickers(webDriver, market, page++)
                if (tickers.isEmpty()) break
                else yieldAll(tickers)
            }
        }.asStream()
    }

    /**
     * 특정 시장, 특정 페이지 모든 티커 크롤링하여 반환
     */
    fun crawlTickers(webDriver: WebDriver, market: Market, page: Int): List<Ticker> {
        val url = NaverFinanceUrls.tickers(market, page)
        logger.debug("Opening $url")
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
            .filter { !it.isEmpty() }
            .map {
                val link = it[headers.indexOf("종목명")].getElementsByTag("a")[0].attr("href")
                val code = "(?<=code=)[A-Za-z0-9]+".toRegex().find(link)!!.value
                Ticker(
                    market = market,
                    code = code,
                    name = it[headers.indexOf("종목명")].text().replace(",", ""),
                    price = it[headers.indexOf("현재가")].text().replace(",", "").toInt(),
                    marketCap = it[headers.indexOf("시가총액")].text().replace(",", "").toLong() * 1_0000_0000,
                    shares = it[headers.indexOf("상장주식수")].text().replace(",", "").toLong() * 1000,
                    per = it[headers.indexOf("PER")].text().replace(",", "").parseDouble() ?: Double.NaN,
                    roe = it[headers.indexOf("ROE")].text().replace(",", "").parseDouble() ?: Double.NaN,
                    externalLinks = makeExternalLinkSets(code),
                )
            }
    }

    fun crawlFinanceSummary(
        webDriver: WebDriver,
        tabBy: By,
        period: CorpStat.FinanceSummary.Period,
    ): Pair<CorpStat.FinanceSummary.Period, CorpStat.FinanceSummary> {
        val tab = webDriver.findElement(tabBy)
        val actions = Actions(webDriver)
        actions.click(tab).build().perform()
        WebDriverWait(webDriver, 10).until(ExpectedCondition { driver ->
            driver!!.findElement(tabBy).getAttribute("class").split(" ").contains("on")
        })

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
                "[0-9]{4}/[0-9]{2}".toRegex()
                    .find(it.text())
                    ?.value
                    ?.split("/")
                    ?.let { spl -> YearMonth.of(spl[0].toInt(), spl[1].toInt()) }
            }

        val financeSummary = CorpStat.FinanceSummary()

        table
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr").associate { element ->
                val title = element.getElementsByTag("th")[0].text()
                val values = element.getElementsByTag("td").map { td -> td.text() }
                title.trim() to values
            }.apply {
                doIfNotNull("매출액") { value -> financeSummary.sales.set(headers, value.parseAnd100MillionTimes()) }
                doIfNotNull("영업이익") { value -> financeSummary.operatingProfit.set(headers, value.parseAnd100MillionTimes() )}
                doIfNotNull("당기순이익") { value -> financeSummary.netProfit.set(headers, value.parseAnd100MillionTimes() )}
                doIfNotNull("당기순이익(지배)") { value -> financeSummary.controllingInterest.set(headers, value.parseAnd100MillionTimes() )}
                doIfNotNull("ROE(%)") { value -> financeSummary.roe.set(headers, value.map { it.parseDouble() } )}
                doIfNotNull("EPS(원)") { value -> financeSummary.eps.set(headers, value.map { it.parseDouble()?.toLong() } )}
                doIfNotNull("PER(배)") { value -> financeSummary.per.set(headers, value.map { it.parseDouble() } )}
                doIfNotNull("발행주식수(보통주)") { value -> financeSummary.issuedCommonShares.set(headers,value.map { it.parseDouble()?.toLong() } )}
            }

        return period to financeSummary
    }

    fun crawlCorpStat(webDriver: WebDriver, code: String): CorpStat {
        val url = NaverFinanceUrls.companyInfo(code)
        logger.debug("Opening $url")
        webDriver.get(url)

        val html = webDriver.findElement(By.xpath("/html"))!!
            .getAttribute(OUTER_HTML)
            .run { Jsoup.parse(this) }

        val investOpinionElement = html.getElementById("cTB15")!!
        val investOpinion = try {
            investOpinionElement.getElementsByTag("th")
                .subList(1, 5)
                .map { it.text().split("(")[0] }
                .zip(
                    investOpinionElement.getElementsByTag("td")
                        .subList(2, 6)
                        .map { element -> element.text() }
                )
                .toMap()
                .let { investOpinionMap ->
                    CorpStat.InvestOpinion(
                        targetPrice = investOpinionMap["목표주가"]?.parseDouble()?.toInt(),
                        eps = investOpinionMap["EPS"]?.parseDouble()?.toInt(),
                        per = investOpinionMap["PER"]?.parseDouble(),
                        numberOfOrgans = investOpinionMap["추정기관수"]?.parseDouble()?.toInt()
                    )
                }
        } catch (e: Throwable) {
            null
        }

        val financeSummaries = mapOf(
            crawlFinanceSummary(webDriver, By.id("cns_td21"), CorpStat.FinanceSummary.Period.YEAR),
            crawlFinanceSummary(webDriver, By.id("cns_td22"), CorpStat.FinanceSummary.Period.QUARTER),
        )

        return CorpStat(
            code = code,
            financeSummaries = financeSummaries,
            investOpinion = investOpinion
        )

    }

    fun crawlIndustries(webDriver: WebDriver): Stream<NaverFinanceIndustry> {
        // 업종 페이지 입장
        webDriver.get(NaverFinanceUrls.industries())

        // 테이블 파싱
        val trList: Elements =
            webDriver.findElement(By.tagName("table"))
                .getAttribute(OUTER_HTML)
                .run {
                    Jsoup.parse(this).getElementsByTag("tr")
                }

        val industries: List<NaverFinanceIndustry> = trList
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

        return sequence {
            industries.forEach {
                // 업종 상세 페이지 입장
                webDriver.get(NaverFinanceUrls.resolve(it.ref))
                val table = webDriver.findElements(By.tagName("table"))[2].getAttribute(OUTER_HTML)

                it.tickerRefs = Jsoup.parse(table)
                    .getElementsByTag("tbody")[0]
                    .getElementsByTag("tr")
                    .mapNotNull { ele ->
                        val aList = ele.getElementsByTag("a"); if (aList.size > 0) aList[0].attr("href") else null
                    }

                yield(it)
            }
        }.asStream()
    }

    private fun crawlThemes(contentTable: Element): List<NaverFinanceTheme> {
        return contentTable
            .getElementsByTag("tr")
            .filter { it.getElementsByTag("td").text().trim().isNotEmpty() }
            .map {
                val tdList = it.getElementsByTag("td")
                NaverFinanceTheme(
                    name = tdList[0].getElementsByTag("a").text().trim(),
                    ref = tdList[0].getElementsByTag("a").attr("href"),
                    marginRate = tdList[1].text().parseDouble(),
                )
            }
    }

    fun crawlThemes(webDriver: WebDriver): Stream<NaverFinanceTheme> {
        val findThemeTable = { body: Element -> body.getElementsByTag("table").find { it.hasClass("theme") }!! }
        val findLastPage = { body: Element ->
            body.getElementsByTag("td")
                .find { it.hasClass("pgRR") }!!
                .getElementsByTag("a")
                .attr("href")
                .split("page=")[1]
                .toInt()
        }

        val themes: MutableList<NaverFinanceTheme> = mutableListOf()
        var url = NaverFinanceUrls.themes(1)
        logger.debug("Opening $url")
        webDriver.get(url)
        var body = Jsoup.parse(webDriver.findElements(By.tagName("body"))[0].getAttribute(OUTER_HTML))
        val lastPage = findLastPage(body)
        themes.addAll(crawlThemes(findThemeTable(body)))

        (2..lastPage).map { page ->
            url = NaverFinanceUrls.themes(page)
            logger.debug("Opening $url")
            webDriver.get(url)
            body = Jsoup.parse(webDriver.findElements(By.tagName("body"))[0].getAttribute(OUTER_HTML))
            themes.addAll(crawlThemes(findThemeTable(body)))
        }

        return sequence {
            themes.forEach { theme ->
                webDriver.get(NaverFinanceUrls.resolve(theme.ref))
                theme.tickerRefs = Jsoup.parse(webDriver.findElements(By.tagName("body"))[0].getAttribute(OUTER_HTML))
                    .getElementsByTag("table")
                    .find { it.attr("class") == "type_5" }!!
                    .getElementsByTag("tbody")[0]
                    .getElementsByTag("tr")
                    .map { it.getElementsByTag("a") }
                    .filter { it.isNotEmpty() }
                    .map { it[0].attr("href") }
                yield(theme)
            }
        }.asStream()
    }

    fun crawlEtfCodes(webDriver: WebDriver): List<String> {
        val url = NaverFinanceUrls.etf()
        logger.debug("Opening $url")
        webDriver.get(url)
        val html = webDriver.findElement(By.tagName("html")).getAttribute(OUTER_HTML)
        return Jsoup.parse(html)
            .getElementById("etfItemTable")!!
            .getElementsByTag("tr")
            .filter { it.getElementsByTag("td").size > 0 }
            .filter { it.getElementsByTag("td")[0].getElementsByTag("a").size > 0 }
            .map {
                it.getElementsByTag("td")[0]
                    .getElementsByTag("a")[0]
                    .attr("href")
                    .split("code=")[1]
            }
    }

    fun crawlEtnCodes(webDriver: WebDriver): List<String> {
        val url = NaverFinanceUrls.etn()
        logger.debug("Opening $url")
        webDriver.get(url)
        val html = webDriver.findElement(By.tagName("html")).getAttribute(OUTER_HTML)
        return Jsoup.parse(html)
            .getElementById("etnItemTable")!!
            .getElementsByTag("tr")
            .filter { it.getElementsByTag("td").size > 0 }
            .filter { it.getElementsByTag("td")[0].getElementsByTag("a").size > 0 }
            .map {
                it.getElementsByTag("td")[0]
                    .getElementsByTag("a")[0]
                    .attr("href")
                    .split("code=")[1]
            }
    }

    fun crawlFinanceAnal(webDriver: WebDriver, code: String): FinanceAnalysis {
        val url = NaverFinanceUrls.financialAnalysis(code)

        logger.debug("Opening $url")
        webDriver.get(url)

        // 재무재표 상태표 탭 클릭
        val actions = Actions(webDriver)
        val financeStatTab = webDriver.findElement(By.id("rpt_tab2"))
        actions.click(financeStatTab).build().perform()
        WebDriverWait(webDriver, 10).until(ExpectedCondition { driver ->
            driver!!.findElement(By.id("rpt_td2")).getAttribute("class").split(" ").contains("on")
        })
        val html = webDriver.findElement(By.tagName("html")).getAttribute(OUTER_HTML)
        val table = Jsoup.parse(html)
            .getElementsByTag("table").last()!!

        val headers = table
            .getElementsByTag("thead")[0]
            .getElementsByTag("tr")[0]
            .getElementsByTag("th")
            .run { subList(1, size - 2) }
            .map {
                "[0-9]{4}/[0-9]{2}".toRegex()
                    .find(it.text())
                    ?.value
                    ?.split("/")
                    ?.let { spl -> YearMonth.of(spl[0].toInt(), spl[1].toInt()) }
            }

        return table
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr")
            .filter { !it.attr("style").contains("display: none;") }
            .associate { element ->
                val tdList = element.getElementsByTag("td")
                val title = tdList[0].attr("title").trim()
                val values = element.getElementsByTag("td")
                    .subList(1, tdList.size - 2)
                    .map { td -> td.text().ifEmpty { null } }
                title.trim() to values
            }.let { statTable ->
                val financeStat = FinanceAnalysis.FinanceStat()
                statTable.doIfNotNull("유동자산") {
                    financeStat.currentAssets.set(headers, it.parseAnd100MillionTimes())
                }
                statTable.doIfNotNull("유동부채") {
                    financeStat.currentLiabilities.set(headers, it.parseAnd100MillionTimes())
                }
                statTable.doIfNotNull("투자자산") {
                    financeStat.investmentAssets.set(headers, it.parseAnd100MillionTimes())
                }
                statTable.doIfNotNull("비유동부채") {
                    financeStat.nonCurrentLiabilities.set(headers, it.parseAnd100MillionTimes())
                }
                statTable.doIfNotNull("자산총계") {
                    financeStat.totalAssets.set(headers, it.parseAnd100MillionTimes())
                }
                statTable.doIfNotNull("부채총계") {
                    financeStat.totalDebt.set(headers, it.parseAnd100MillionTimes())
                }

                // 순자산
                financeStat.netWorth.set(headers, headers.map { header ->
                    if (header == null) {
                        null
                    } else if (financeStat.totalAssets[header] != null && financeStat.totalDebt[header] != null) {
                        financeStat.totalAssets[header]!! - financeStat.totalDebt[header]!!
                    } else {
                        null
                    }
                })

                FinanceAnalysis(
                    code = code,
                    financeStat = financeStat
                )
            }
    }

    private fun String?.parseAnd100MillionTimes(): Long? =
        this?.parseDouble()?.times(1_0000_0000)?.toLong()

    private fun Collection<String?>.parseAnd100MillionTimes(): List<Long?> =
        this.map { it.parseAnd100MillionTimes() }

    private fun <K, V> Map<K, V>.doIfNotNull(key: K, doSomething: (V) -> Unit) {
        if (this[key] != null) {
            return doSomething(this[key]!!)
        }
    }
}