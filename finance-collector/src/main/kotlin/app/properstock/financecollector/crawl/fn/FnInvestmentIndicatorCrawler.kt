package app.properstock.financecollector.crawl.fn

import app.properstock.financecollector.model.fn.FnInvestmentIndicator
import app.properstock.financecollector.util.HttpSimpleRequester
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.YearMonth


object FnInvestmentIndicatorCrawler {

    private val logger = LoggerFactory.getLogger(FnInvestmentIndicatorCrawler::class.java)

    private fun url(
        tickerCode: String,
        connectIFRS: Boolean = true,
    ): URL {
        val params = listOf(
            "pGB=1",
            "gicode=$tickerCode",
            "cID=",
            "MenuYn=Y",
            "ReportGB=${if (connectIFRS) "D" else "B"}",
            "NewMenuID=105",
            "stkGb=701"
        )
        // ex) https://comp.fnguide.com/SVO2/ASP/SVD_Invest.asp?pGB=1&gicode=A005930&cID=&MenuYn=Y&ReportGB=&NewMenuID=105&stkGb=701
        return URL("https://comp.fnguide.com/SVO2/ASP/SVD_Invest.asp?${params.joinToString("&")}")
    }

    fun crawl(code: String): List<FnInvestmentIndicator>? {
        logger.info("Crawling ${FnInvestmentIndicator::class.simpleName} - $code")
        val html = try {
            HttpSimpleRequester.get(url(code))
        } catch (e: Throwable) {
            logger.warn(e.message)
            return null
        }

        val table = Jsoup
            .parse(html)
            .getElementsByTag("table")
            .find { t -> t.getElementsByTag("caption").any { it.text().contains("기업가치 지표") } }

        if (table == null) {
            logger.warn("No required tag.")
            return null
        }

        val head = table.getElementsByTag("thead")[0].getElementsByTag("th")
            .let { it.subList(1, it.size) }
            .map { it.text() }
            .map { s -> s.split("/").let { YearMonth.of(it[0].toInt(), it[1].toInt()) } }
        return table
            .getElementsByTag("tbody")[0]
            .getElementsByTag("tr")
            .mapNotNull {
                val th = it.getElementsByTag("th")[0]
                val title = th.getElementsByTag("dt").text().trim()
                if (title.isNotBlank()) {
                    val description = th.getElementsByTag("dd")[0].text().trim().replace("\n", "")
                    val values = it.getElementsByTag("td").map { td -> td.text() }
                    values
                        .map(String::doubleOrNull)
                        .mapIndexed { i, v ->
                            if (v != null) {
                                val realValue = if (title in listOf("EV", "EBITDA", "매출액")) v * 10000_0000 else v
                                FnInvestmentIndicator(
                                    code = code,
                                    title = title,
                                    description = description,
                                    yearMonth = head[i],
                                    value = realValue
                                )
                            } else null
                        }
                } else null
            }
            .flatten()
            .filterNotNull()
    }
}

private val String.doubleOrNull
    get() = try {
        replace(",", "").toDouble()
    } catch (e: NumberFormatException) {
        null
    }
