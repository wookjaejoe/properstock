package app.properstock.financecollector.service.proper

import app.properstock.financecollector.model.ProperPrice
import app.properstock.financecollector.model.ProperPriceFormula
import app.properstock.financecollector.repository.ProperPriceRepository
import app.properstock.financecollector.repository.TickerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ProperPriceService(
    private val formulaList: List<ProperPriceFormula>,
    private val tickerRepository: TickerRepository,
    private val properPriceRepository: ProperPriceRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(ProperPriceService::class.java)

    init {
        // 공식 심볼 중복 확인
        if (formulaList.map { it.symbol }.toSet().size != formulaList.size) {
            throw VerifyError("Some of the symbols of the formula are duplicated")
        }
    }

    fun updateAll() {
        logger.info("Starting to update properPrice for all tickers...")
        val tickers = tickerRepository.findAll()
        tickers.forEachIndexed { index, ticker ->
            logger.info("[${index + 1}/${tickers.size}] Starting to update properPrice@${ticker.code}...")
            update(ticker.code)
        }
    }

    fun calculate(code: String): Map<String, ProperPriceFormula.Output> {
        return formulaList.associate { it.symbol to it.calculate(code) }
    }

    fun update(code: String) {
        calculate(code).forEach { update(code, it.key, it.value) }
    }

    private fun update(tickerCode: String, formulaSymbol: String, formulaOut: ProperPriceFormula.Output): ProperPrice {
        val oldData = properPriceRepository.findByTickerCodeAndFormulaSymbol(tickerCode, formulaSymbol)
        return if (oldData != null) {
            oldData.value = formulaOut.value
            oldData.arguments = formulaOut.arguments
            oldData.note = formulaOut.note
            properPriceRepository.save(oldData)
        } else {
            val newData = ProperPrice(
                tickerCode = tickerCode,
                formulaSymbol = formulaSymbol,
                value = formulaOut.value,
                arguments = formulaOut.arguments,
                note = formulaOut.note
            )
            properPriceRepository.save(newData)
        }
    }
}
