package app.properstock.financecollector.service.proper

import app.properstock.financecollector.model.ProperPrice
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.ProperPriceRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.proper.formula.EpsMultipliedByPer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class ProperPriceService(
    val tickerRepository: TickerRepository,
    val financeAnalysisRepository: FinanceAnalysisRepository,
    val properPriceRepository: ProperPriceRepository,
    val epsMultipliedByPer: EpsMultipliedByPer
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ProperPriceService::class.java)
    }

    fun updateAll() {
        logger.info("Starting to update properPrice for all tickers...")
        tickerRepository.findAll().forEach {
            update(it.code)
        }
    }

    fun update(code: String) {
        logger.info("Starting to update properPrice@$code...")
        val financeAnalysis = financeAnalysisRepository.findByCode(code) ?: return
        logger.info("Calculating ${epsMultipliedByPer.symbol}...")
        val formulaOut = epsMultipliedByPer.calculate(
            epsList = financeAnalysis.financeSummary.eps.data.toSortedMap(),
            perList = financeAnalysis.financeSummary.per.data.toSortedMap()
        )

        logger.info("Updating properPrice:${epsMultipliedByPer.symbol}@$code")
        val oldData = properPriceRepository.findByTickerCodeAndFormulaSymbol(code, epsMultipliedByPer.symbol)
        if (oldData != null) {
            oldData.value = formulaOut.value
            oldData.note = formulaOut.note
            properPriceRepository.save(oldData)
        } else {
            val newData = ProperPrice(
                tickerCode = code,
                formulaSymbol = epsMultipliedByPer.symbol,
                value = formulaOut.value,
                note = formulaOut.note
            )
            properPriceRepository.save(newData)
        }
    }
}

@Component
interface ProperPriceFormula {
    val symbol: String
    val title: String
    val description: String

    class Output(
        val value: Double,
        val note: String? = null
    ) {
        companion object {
            fun dummy(note: String) = Output(Double.NaN, note)
        }
    }
}

@Component
class FormulaVerifier(
    formulaList: List<ProperPriceFormula>
) {
    init {
        // 공식 심볼 중복 확인
        if (formulaList.map { it.symbol }.toSet().size != formulaList.size) {
            throw VerifyError("Some of the symbols of the formula are duplicated")
        }
    }
}