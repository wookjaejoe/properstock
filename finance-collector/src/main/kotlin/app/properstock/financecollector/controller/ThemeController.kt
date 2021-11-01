package app.properstock.financecollector.controller

import app.properstock.financecollector.model.Theme
import app.properstock.financecollector.repository.ThemeRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/themes")
class ThemeController(
    val themeRepository: ThemeRepository
) {
    @GetMapping
    fun getAll(): MutableList<Theme> {
        return themeRepository.findAll()
    }
}