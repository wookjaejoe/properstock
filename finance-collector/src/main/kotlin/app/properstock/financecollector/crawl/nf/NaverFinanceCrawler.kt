package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.crawl.WebDriverConnector
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
import java.time.Instant
import java.time.YearMonth
import java.util.stream.Stream
import kotlin.streams.asStream

const val INNER_HTML = "innerHTML"
const val OUTER_HTML = "outerHTML"

fun String.parseDouble(): Double? = try {
    "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?".toRegex().find(this.trim().replace(",", ""))?.value?.toDouble() ?: Double.NaN
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
        logger.debug("Crawling all tickers started.")
        return sequence {
            for (ticker in crawlTickers(Market.KOSPI)) yield(ticker)
            for (ticker in crawlTickers(Market.KOSDAQ)) yield(ticker)
        }.asStream()
    }

    /**
     * 특정 시장의 모든 티커 목록 크롤링하여 반환
     */
    fun crawlTickers(market: Market): Stream<Ticker> {
        logger.debug("Crawling ticker in ${market.name} started.")
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
        return webDriverConnector.connect {
            logger.debug("Opening $url")
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
                        updated = Instant.now()
                    )
                }
        }
    }

    fun crawlFinanceSummary(
        webDriver: WebDriver,
        tabBy: By,
        period: CorpStat.FinanceSummary.Period
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
                val str = "[0-9]{4}/[0-9]{2}".toRegex().find(it.text())?.value!!
                val spl = str.split("/")
                YearMonth.of(spl[0].toInt(), spl[1].toInt())
            }

        val financeSummary = CorpStat.FinanceSummary(period)

        table
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr").map { element ->
                val title = element.getElementsByTag("th")[0].text()
                val values = element.getElementsByTag("td").map { td -> td.text() }
                Pair(title, values)
            }.associate {
                Pair(it.first.trim(), it.second)
            }.run {
                financeSummary.sales.set(
                    headers,
                    this["매출액"]!!.map { it.parseDouble()?.times(1_0000_0000)?.toLong() }
                )
                financeSummary.operatingProfit.set(
                    headers,
                    this["영업이익"]!!.map { it.parseDouble()?.times(1_0000_0000)?.toLong() }
                )
                financeSummary.netProfit.set(
                    headers,
                    this["당기순이익"]!!.map { it.parseDouble()?.times(1_0000_0000)?.toLong() }
                )
                financeSummary.controllingInterest.set(
                    headers,
                    this["당기순이익(지배)"]!!.map { it.parseDouble()?.times(1_0000_0000)?.toLong() }
                )
                financeSummary.roe.set(
                    headers,
                    this["ROE(%)"]!!.map { it.parseDouble() }
                )
                financeSummary.eps.set(
                    headers,
                    this["EPS(원)"]!!.map { it.parseDouble()?.toLong() }
                )
                financeSummary.per.set(
                    headers,
                    this["PER(배)"]!!.map { it.parseDouble() }
                )
                financeSummary.issuedCommonShares.set(
                    headers,
                    this["발행주식수(보통주)"]!!.map { it.parseDouble()?.toLong() }
                )
            }
        return Pair(period, financeSummary)
    }

    fun crawlCorpStat(code: String): CorpStat {
        val url = NaverFinanceUrls.companyInfo(code)
        return webDriverConnector.connect {
            logger.debug("Opening $url")
            get(url)

            val html = findElement(By.xpath("/html"))!!
                .getAttribute(OUTER_HTML)
                .run { Jsoup.parse(this) }

            val investOpinionElement = html.getElementById("cTB15")!!
            val investOpinionMap = investOpinionElement.getElementsByTag("th")
                .subList(1, 5)
                .map { it.text().split("(")[0] }
                .zip(
                    investOpinionElement.getElementsByTag("td")
                        .subList(2, 6)
                        .map { element -> element.text() }
                )
                .toMap()
            val investOpinion = CorpStat.InvestOpinion(
                targetPrice = investOpinionMap["목표주가"]?.parseDouble()?.toInt(),
                eps = investOpinionMap["EPS"]?.parseDouble()?.toInt(),
                per = investOpinionMap["PER"]?.parseDouble(),
                numberOfOrgans = investOpinionMap["추정기관수"]?.parseDouble()?.toInt()
            )

            val financeSummaries = mapOf(
                crawlFinanceSummary(this, By.id("cns_td21"), CorpStat.FinanceSummary.Period.YEAR),
                crawlFinanceSummary(this, By.id("cns_td22"), CorpStat.FinanceSummary.Period.QUARTER),
            )

            CorpStat(
                code = code,
                financeSummaries = financeSummaries,
                investOpinion = investOpinion
            )
        }
    }

    fun crawlIndustries(): Stream<NaverFinanceIndustry> {
        val industries: List<NaverFinanceIndustry> = webDriverConnector.connect {
            // 업종 페이지 입장
            get(NaverFinanceUrls.industries())

            // 테이블 파싱
            val trList: Elements =
                findElement(By.tagName("table"))
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
                val ind: NaverFinanceIndustry = webDriverConnector.connect {
                    // 업종 상세 페이지 입장
                    get(NaverFinanceUrls.resolve(it.ref))
                    val table = findElements(By.tagName("table"))[2].getAttribute(OUTER_HTML)

                    it.tickerRefs = Jsoup.parse(table)
                        .getElementsByTag("tbody")[0]
                        .getElementsByTag("tr")
                        .mapNotNull {
                            val aList = it.getElementsByTag("a"); if (aList.size > 0) aList[0].attr("href") else null
                        }
                    it
                }

                yield(ind)
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

    fun crawlThemes(): Stream<NaverFinanceTheme> {
        var lastPage: Int
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
        webDriverConnector.connect {
            var url = NaverFinanceUrls.themes(1)
            logger.debug("Opening $url")
            get(url)
            var body = Jsoup.parse(findElements(By.tagName("body"))[0].getAttribute(OUTER_HTML))
            lastPage = findLastPage(body)
            themes.addAll(crawlThemes(findThemeTable(body)))

            (2..lastPage).map { page ->
                url = NaverFinanceUrls.themes(page)
                logger.debug("Opening $url")
                get(url)
                body = Jsoup.parse(findElements(By.tagName("body"))[0].getAttribute(OUTER_HTML))
                themes.addAll(crawlThemes(findThemeTable(body)))
            }
        }

        return sequence {
            themes.forEach { theme ->
                webDriverConnector.connect {
                    get(NaverFinanceUrls.resolve(theme.ref))
                    val body = Jsoup.parse(findElements(By.tagName("body"))[0].getAttribute(OUTER_HTML))
                    theme.tickerRefs = body.getElementsByTag("table")
                        .find { it.attr("class") == "type_5" }!!
                        .getElementsByTag("tbody")[0]
                        .getElementsByTag("tr")
                        .map { it.getElementsByTag("a") }
                        .filter { it.isNotEmpty() }
                        .map { it[0].attr("href") }
                    theme
                }.run {
                    yield(theme)
                }
            }
        }.asStream()
    }

    fun crawlEtfCodes(): List<String> {
        val url = NaverFinanceUrls.etf()
        return webDriverConnector.connect {
            logger.debug("Opening $url")
            get(url)
            val html = findElement(By.tagName("html")).getAttribute(OUTER_HTML)
            Jsoup.parse(html)
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
    }

    fun crawlEtnCodes(): List<String> {
        val url = NaverFinanceUrls.etn()
        return webDriverConnector.connect {
            logger.debug("Opening $url")
            get(url)
            val html = findElement(By.tagName("html")).getAttribute(OUTER_HTML)
            Jsoup.parse(html)
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
    }

    fun crawlFinanceAnal(code: String): FinanceAnalysis {
        val url = NaverFinanceUrls.financialAnalysis(code)
        val financeStat = webDriverConnector.connect {
            logger.debug("Opening $url")
            get(url)

            // 재무재표 상태표 탭 클릭
            val actions = Actions(this)
            val financeStatTab = findElement(By.id("rpt_tab2"))
            actions.click(financeStatTab).build().perform()
            WebDriverWait(this, 10).until(ExpectedCondition { driver ->
                driver!!.findElement(By.id("rpt_td2")).getAttribute("class").split(" ").contains("on")
            })
            val html = findElement(By.tagName("html")).getAttribute(OUTER_HTML)
            val table = Jsoup.parse(html)
                .getElementsByTag("table").last()!!

            val headers = table
                .getElementsByTag("thead")[0]
                .getElementsByTag("tr")[0]
                .getElementsByTag("th")
                .run { subList(1, size - 2) }
                .map {
                    val str = "[0-9]{4}/[0-9]{2}".toRegex().find(it.text())?.value!!
                    val spl = str.split("/")
                    YearMonth.of(spl[0].toInt(), spl[1].toInt())
                }

            table
                .getElementsByTag("tbody")[0]
                .getElementsByTag("tr")
                .filter { !it.attr("style").contains("display: none;") }
                .map { element ->
                    val tdList = element.getElementsByTag("td")
                    val title = tdList[0].attr("title").trim()
                    val values = element.getElementsByTag("td")
                        .subList(1, tdList.size - 2)
                        .map { td -> td.text().ifEmpty { null } }
                    Pair(title, values)
                }.associate {
                    Pair(it.first.trim(), it.second)
                }.run {
                    val financeStat = FinanceAnalysis.FinanceStat()
                    financeStat.currentAssets.set(headers, this["유동자산"]!!.map { it?.parseDouble()?.times(1_0000_0000)?.toLong() })
                    financeStat.currentLiabilities.set(headers, this["유동부채"]!!.map { it?.parseDouble()?.times(1_0000_0000)?.toLong() })
                    financeStat.investmentAssets.set(headers, this["투자자산"]!!.map { it?.parseDouble()?.times(1_0000_0000)?.toLong() })
                    financeStat.nonCurrentLiabilities.set(headers, this["비유동부채"]!!.map { it?.parseDouble()?.times(1_0000_0000)?.toLong() })
                    financeStat
                }
        }

        return FinanceAnalysis(
            code = code,
            financeStat = financeStat
        )
    }
}