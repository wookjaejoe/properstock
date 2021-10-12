package app.properstock.financecollector.service

import app.properstock.financecollector.repository.FinanceAnalysisRepository
import app.properstock.financecollector.repository.TickerRepository
import org.springframework.stereotype.Service
import kotlin.math.round

@Service
class FinanceProcessor(
    val tickerRepository: TickerRepository,
    val financeAnalysisRepository: FinanceAnalysisRepository
) {

    fun processFinanceAnalysis(code: String) {
        tickerRepository.findByCode(code)
            .block()
            .run {
                println(this)
            }

        financeAnalysisRepository.findByCode(code)
            .block()
            .run {
                val yearMonths = this!!.financeSummary.eps.values.map { it.key }
                for(yearMonth in yearMonths) {
                    val pst = this.financeSummary.eps.values[yearMonth]?.times(this.financeSummary.per.values[yearMonth]!!)
                    println("$yearMonth: ${pst?.let { round(it) }}")
                }
            }
    }
}