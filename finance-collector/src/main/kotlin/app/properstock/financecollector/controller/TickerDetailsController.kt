package app.properstock.financecollector.controller

import app.properstock.financecollector.exception.ResourceNotFoundException
import app.properstock.financecollector.model.TickerDetail
import app.properstock.financecollector.repository.CorpStatRepository
import app.properstock.financecollector.repository.FinanceAnalRepository
import app.properstock.financecollector.repository.ProperPriceRepository
import app.properstock.financecollector.repository.TickerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ticker-details")
class TickerDetailsController(
    val tickerRepository: TickerRepository,
    val properPriceRepository: ProperPriceRepository,
    val financeAnalRepository: FinanceAnalRepository,
    val corpStatRepository: CorpStatRepository
) {
    @GetMapping("/{code}")
    fun getOne(@PathVariable code: String): TickerDetail {
        val ticker = tickerRepository.findByCode(code) ?: throw ResourceNotFoundException("Ticker not found: $code")
        return TickerDetail(
            ticker = ticker,
            properPrices = properPriceRepository.findAllByTickerCodeIn(listOf(code)),
            financeAnalysis = financeAnalRepository.findByCode(code),
            corpStat = corpStatRepository.findByCode(code)
        )
    }
}