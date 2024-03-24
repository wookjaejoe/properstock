package app.properstock.financecollector.crawl.fn

import app.properstock.financecollector.base.defaultObjectMapper
import app.properstock.financecollector.model.fn.FnTicker
import app.properstock.financecollector.util.HttpSimpleRequester
import com.fasterxml.jackson.core.type.TypeReference
import java.net.URL

/**
 * https://comp.fnguide.com 종목 검색 기능
 */
object FnTickerFinder {
    private fun url(
        searchKey: String,
        onlyCompany: Boolean = true,
    ): URL {
        val params = listOf(
            "mkt_gb=1",  // 마켓 타입: 1은 전
            "comp_gb=${if (onlyCompany) 1 else 0}",
            "s_type=1",  // ?
            "search_key1=$searchKey"
        )
        return URL("https://comp.fnguide.com/SVO2/common/lookup_data.asp?${params.joinToString("&")}")
    }

    fun findAll(searchKey: String = ""): List<FnTicker> =
        HttpSimpleRequester.get(url(searchKey)).let { searchResult ->
            defaultObjectMapper.readValue(searchResult, object : TypeReference<List<FnTicker>>() {})
        }
}