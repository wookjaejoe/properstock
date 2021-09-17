package app.properstock.crawl.nf

import app.properstock.exception.NotSupportedException
import app.properstock.model.Market

class NaverFinanceUrls {
    fun tickers(market: Market, page: Int): String {
        val sosok = when (market) {
            Market.KOSPI -> 0
            Market.KOSDAQ -> 1
            else -> throw NotSupportedException("Not supported market. Maybe programmer's mistake.")
        }

        return "https://finance.naver.com/sise/sise_market_sum.nhn?sosok=$sosok&page=$page"
    }

    fun coInfo(code: String) = "https://finance.naver.com/item/coinfo.naver?code=$code"
}