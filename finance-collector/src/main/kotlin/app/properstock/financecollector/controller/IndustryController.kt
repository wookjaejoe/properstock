package app.properstock.financecollector.controller

import app.properstock.financecollector.model.Industry
import app.properstock.financecollector.repository.IndustryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/industries")
class IndustryController(
    val industryRepository: IndustryRepository
) {
    @GetMapping
    fun getAll(): List<Industry> {
        return industryRepository.findAll()
    }
}