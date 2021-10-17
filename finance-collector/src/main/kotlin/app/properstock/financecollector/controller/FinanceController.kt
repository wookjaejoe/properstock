package app.properstock.financecollector.controller

import app.properstock.financecollector.model.FinanceAnalysis
import app.properstock.financecollector.repository.FinanceAnalysisRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/finances")
class FinanceController(
    val financeAnalysisRepository: FinanceAnalysisRepository
) {
    @GetMapping
    fun getBasics(): Flux<FinanceAnalysis> {
        return financeAnalysisRepository.findAll()
    }
}