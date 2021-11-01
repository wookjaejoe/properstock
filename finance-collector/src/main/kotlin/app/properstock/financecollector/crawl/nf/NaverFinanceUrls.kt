package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.exception.NotSupportedException
import app.properstock.financecollector.model.Market
import org.springframework.stereotype.Component

class NaverFinanceUrls {

    companion object {
        const val root = "https://finance.naver.com"

        fun resolve(subPath: String): String {
            return "$root${if (subPath.startsWith("/")) subPath else "/$subPath"}"
        }

        fun tickers(market: Market, page: Int): String {
            val sosok = when (market) {
                Market.KOSPI -> 0
                Market.KOSDAQ -> 1
                else -> throw NotSupportedException("Not supported market. Maybe programmer's mistake.")
            }
            return resolve("/sise/sise_market_sum.nhn?sosok=$sosok&page=$page")
        }

        fun companyInfo(code: String) = "https://navercomp.wisereport.co.kr/v2/company/c1010001.aspx?cmp_cd=$code"
        fun industries() = resolve("/sise/sise_group.naver?type=upjong")
        fun themes(page: Int) = resolve("/sise/theme.naver?page=$page")
    }
}