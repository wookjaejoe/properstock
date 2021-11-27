package app.properstock.financecollector.controller

import app.properstock.financecollector.model.ProperPriceFormula
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/proper/formulas")
class ProperPriceFormulaController(
    val formulaList: List<ProperPriceFormula>,
) {
    @GetMapping
    fun getAll(): List<ProperPriceFormula.Dto> {
        return formulaList.map { ProperPriceFormula.mapper.toDto(it) }
    }
}