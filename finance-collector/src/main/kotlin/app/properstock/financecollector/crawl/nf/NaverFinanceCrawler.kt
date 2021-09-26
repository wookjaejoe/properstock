package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.model.Market
import app.properstock.financecollector.model.Ticker
import app.properstock.financecollector.service.DbSeqGenerator
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Instant

const val INNER_HTML = "innerHTML"
const val OUTER_HTML = "outerHTML"

@Component
class NaverFinanceCrawler(
    private val driver: ChromeDriver,
    private val dbSeqGenerator: DbSeqGenerator
) {
    private val actions = Actions(driver)
    private val urls = NaverFinanceUrls()

    /**
     * 네이버 파이낸스에서 크롤링 가능한 모든 티커 목록을 크롤링하여 반환
     */
    fun crawlAllTickers(): Flux<Ticker> {
        return Flux.concat(
            crawlTickers(Market.KOSPI),
            crawlTickers(Market.KOSDAQ)
        )
    }

    /**
     * 특정 시장의 모든 티커 목록 크롤링하여 반환
     */
    fun crawlTickers(market: Market): Flux<Ticker> {
        return Flux.create { sink ->
            var page = 1
            while (true) {
                val tickers = crawlTickers(market, page++)
                    .collectList()
                    .block()
                    .orEmpty()
                    .map { sink.next(it); it }

                if (tickers.isEmpty()) break
            }

            sink.complete()
        }
    }

    /**
     * 특정 시장, 특정 페이지 모든 티커 크롤링하여 반환
     */
    fun crawlTickers(market: Market, page: Int): Flux<Ticker> {
        val url = urls.tickers(market, page)
        return Flux.create { sink ->
            driver.get(url)

            // 테이블 탐색
            val tableHtml = driver.findElements(By.tagName("table"))
                .find {
                    try {
                        it.findElement(By.tagName("caption")).getAttribute(INNER_HTML) == "코스피"
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
                        id = null,
                        market = market,
                        code = "(?<=code=)[A-Za-z0-9]+".toRegex().find(link)!!.value,
                        name = it[headers.indexOf("종목명")].text().replace(",", ""),
                        price = it[headers.indexOf("현재가")].text().replace(",", "").toInt(),
                        marketCap = it[headers.indexOf("시가총액")].text().replace(",", "").toLong(),
                        shares = it[headers.indexOf("상장주식수")].text().replace(",", "").toInt(),
                        link = link,
                        updated = Instant.now()
                    )
                }
                .forEach { sink.next(it) }

            sink.complete()
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

        val tableHtml = driver.findElements(By.tagName("table")).find {
            try {
                it.findElement(By.tagName("caption")).getAttribute(INNER_HTML) == "주요재무정보"
            } catch (e: Throwable) {
                false
            }
        }!!.getAttribute(OUTER_HTML)
        return ""
    }
}