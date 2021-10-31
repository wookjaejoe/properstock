package app.properstock.financecollector.controller

import app.properstock.financecollector.model.Market
import app.properstock.financecollector.model.ProperPrice
import app.properstock.financecollector.repository.ProperPriceRepository
import app.properstock.financecollector.repository.TickerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/proper/prices")
class ProperPriceController(
    val tickerRepository: TickerRepository,
    val properPriceRepository: ProperPriceRepository
) {
    @GetMapping
    fun getAll(
        market: Market?,
        industries: Array<String>?,
        themes: Array<String>?,
        formulaSymbol: String?,
        limit: Int?,
        searchText: String?,
    ): List<ProperPrice.Dto> {
        // 종목 필터
        var tickers = tickerRepository.findAll()
        if (market != null) tickers = tickers.filter { it.market == market }
        if (industries != null) tickers = tickers.filter { industries.contains(it.industry) }
        if (themes != null) tickers = tickers.filter { themes.intersect(it.themes).isNotEmpty() }
        if (searchText != null) tickers = tickers.filter { it.name.contains(searchText) || it.code.contains(searchText) }

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
}