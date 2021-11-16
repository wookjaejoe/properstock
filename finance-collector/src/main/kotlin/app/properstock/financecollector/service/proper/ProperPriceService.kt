package app.properstock.financecollector.service.proper

import app.properstock.financecollector.model.ProperPrice
import app.properstock.financecollector.repository.CorpStatRepository
import app.properstock.financecollector.repository.ProperPriceRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.proper.formula.ControllingInterestMultipliedByPer
import app.properstock.financecollector.service.proper.formula.EpsMultipliedByPer
import app.properstock.financecollector.service.proper.formula.EpsMultipliedByRoe
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ProperPriceService(
    formulaList: List<ProperPriceFormula>,
    val tickerRepository: TickerRepository,
    val corpStatRepository: CorpStatRepository,
    val properPriceRepository: ProperPriceRepository,

    val epsMultipliedByPer: EpsMultipliedByPer,
    val epsMultipliedByRoe: EpsMultipliedByRoe,
    val controllingInterestMultipliedByPer: ControllingInterestMultipliedByPer
) {
    init {
        // 공식 심볼 중복 확인
        if (formulaList.map { it.symbol }.toSet().size != formulaList.size) {
            throw VerifyError("Some of the symbols of the formula are duplicated")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ProperPriceService::class.java)
    }

    fun updateAll() {
        logger.info("Starting to update properPrice for all tickers...")
        tickerRepository.findAll().forEach {
            update(it.code)
        }
    }

    fun calculate(code: String): Map<String, ProperPriceFormula.Output> {
        return mapOf(
            epsMultipliedByPer.symbol to epsMultipliedByPer.calculate(code),
            epsMultipliedByRoe.symbol to epsMultipliedByRoe.calculate(code),
            controllingInterestMultipliedByPer.symbol to controllingInterestMultipliedByPer.calculate(code)
        )
    }

    fun update(code: String) {
        logger.info("Starting to update properPrice@$code...")
        calculate(code).forEach { update(code, it.key, it.value) }
    }

    private fun update(tickerCode: String, formulaSymbol: String, formulaOut: ProperPriceFormula.Output): ProperPrice {
        val oldData = properPriceRepository.findByTickerCodeAndFormulaSymbol(tickerCode, formulaSymbol)
        return if (oldData != null) {
            oldData.value = formulaOut.value
            oldData.note = formulaOut.note
            oldData.updated = Instant.now()
            properPriceRepository.save(oldData).apply {
                logger.info("Proper price updated: $oldData")
            }
        } else {
            val newData = ProperPrice(
                tickerCode = tickerCode,
                formulaSymbol = formulaSymbol,
                value = formulaOut.value,
                note = formulaOut.note
            )
            properPriceRepository.save(newData).apply {
                logger.info("New proper price: $newData")
            }
        }
    }
}

@Component
interface ProperPriceFormula {
    val symbol: String
    val title: String
    val shortDescription: String
    val longDescription: String

    fun calculate(code: String): Output

    class Output(
        val value: Double,
        val note: String? = null
    ) {
        companion object {
            fun dummy(note: String) = Output(Double.NaN, note)
        }
    }
}
