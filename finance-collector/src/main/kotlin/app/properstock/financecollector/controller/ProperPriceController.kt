package app.properstock.financecollector.controller

import app.properstock.financecollector.model.Market
import app.properstock.financecollector.model.ProperPrice
import app.properstock.financecollector.repository.ProperPriceRepository
import app.properstock.financecollector.repository.TickerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.full.declaredMemberProperties

@RestController
@RequestMapping("/proper/prices")
class ProperPriceController(
    val tickerRepository: TickerRepository,
    val properPriceRepository: ProperPriceRepository
) {
    @GetMapping
    fun getAll(
        markets: Array<Market>?,
        industries: Array<String>?,
        themes: Array<String>?,
        formulaSymbol: String?,
        limit: Int?,
        searchText: String?,
    ): List<ProperPrice.Dto> {
        // 종목 필터
        var tickers = tickerRepository.findAll()
        if (markets != null) tickers = tickers.filter { markets.contains(it.market) }
        if (industries != null) tickers = tickers.filter { industries.contains(it.industry) }
        if (themes != null) tickers = tickers.filter { themes.intersect(it.themes).isNotEmpty() }
        if (searchText != null) tickers =
            tickers.filter { it.name.contains(searchText, ignoreCase = true) || it.code.contains(searchText, ignoreCase = true) }

        // 기타 필터
        val tickersByCode = tickers.associateBy { it.code }
        var properPrices = properPriceRepository.findAllByTickerCodeInAndValueNot(tickers.map { it.code }, Double.NaN)
        if (formulaSymbol != null) properPrices = properPrices.filter { it.formulaSymbol == formulaSymbol }
        if (limit != null) properPrices = properPrices.take(limit)
        return properPrices.map {
            val ticker = tickersByCode[it.tickerCode]!!
            ProperPrice.mapper.toDto(
                it,
                currentPrice = ticker.price.toDouble(),
                tickerName = ticker.name,
                tickerIndustry = ticker.industry,
                tickerThemes = ticker.themes,
                tickerMarket = ticker.market,
                margin = it.value - ticker.price,
                marginRate = (it.value - ticker.price) / ticker.price * 100
            )
        }.sortedBy { it.marginRate }.reversed()
    }

    @GetMapping("/report.csv")
    fun report(response: HttpServletResponse) {
        println()
        val properPriceProperties = ProperPrice::class.declaredMemberProperties
        response.contentType = "text/csv;charset=utf-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"")
        val header = mutableListOf<String>()
        properPriceProperties.forEach { header.add(it.name) }
        listOf("name", "market", "industry").forEach { header.add(it) }
        response.writer.println(header.joinToString(","))
        val tickers = tickerRepository.findAll()
        properPriceRepository.findAll()
            .forEach { properPrice ->
                val ticker = tickers.find { it.code == properPrice.tickerCode } ?: return
                val row = mutableListOf<String>()
                row.addAll(
                    properPriceProperties.map {
                        val value = it.getter.call(properPrice)
                        val formattedValue = if (value is String) {
                            "\"$value\""
                        } else {
                            value.toString()
                        }
                        formattedValue
                    }
                )

                row.add(ticker.name)
                row.add(ticker.market.name)
                row.add(ticker.industry ?: "")
                response.writer.println(row.joinToString(","))
            }
    }
}