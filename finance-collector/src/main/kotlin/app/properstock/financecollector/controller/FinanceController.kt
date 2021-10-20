package app.properstock.financecollector.controller

import app.properstock.financecollector.model.FinanceAnalysis
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/finances")
class FinanceController(
    val financeAnalysisRepository: FinanceAnalysisRepository
) {
    @GetMapping
    fun getBasics(): List<FinanceAnalysis> {
        return financeAnalysisRepository.findAll()
    }
}