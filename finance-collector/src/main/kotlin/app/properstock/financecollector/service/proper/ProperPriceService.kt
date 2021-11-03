package app.properstock.financecollector.service.proper

import app.properstock.financecollector.model.ProperPrice
import app.properstock.financecollector.repository.FinanceAnalysisRepository
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
    val financeAnalysisRepository: FinanceAnalysisRepository,
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

    fun update(code: String) {
        logger.info("Starting to update properPrice@$code...")
        val financeAnalysis = financeAnalysisRepository.findByCode(code) ?: return

        epsMultipliedByPer.calculate(
            epsList = financeAnalysis.financeSummary.eps.data.toSortedMap(),
            perList = financeAnalysis.financeSummary.per.data.toSortedMap()
        ).run { update(code, epsMultipliedByPer.symbol, this) }

        epsMultipliedByRoe.calculate(
            epsList = financeAnalysis.financeSummary.eps.data.toSortedMap(),
            roeList = financeAnalysis.financeSummary.roe.data.toSortedMap()
        ).run { update(code, epsMultipliedByRoe.symbol, this) }

        controllingInterestMultipliedByPer.calculate(
            controllingInterestList = financeAnalysis.financeSummary.controllingInterest.data.toSortedMap(),
            perList = financeAnalysis.financeSummary.per.data.toSortedMap()
        ).run { update(code, controllingInterestMultipliedByPer.symbol, this) }
    }

    private fun update(tickerCode: String, formulaSymbol: String, formulaOut: ProperPriceFormula.Output): ProperPrice {
        val oldData = properPriceRepository.findByTickerCodeAndFormulaSymbol(tickerCode, formulaSymbol)
        return if (oldData != null) {
            oldData.value = formulaOut.value
            oldData.note = formulaOut.note
            oldData.updated = Instant.now()
            properPriceRepository.save(oldData)
        } else {
            val newData = ProperPrice(
                tickerCode = tickerCode,
                formulaSymbol = formulaSymbol,
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
    val shortDescription: String
    val longDescription: String

    class Output(
        val value: Double,
        val note: String? = null
    ) {
        companion object {
            fun dummy(note: String) = Output(Double.NaN, note)
        }
    }
}
