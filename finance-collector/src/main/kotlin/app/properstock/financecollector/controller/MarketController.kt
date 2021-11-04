package app.properstock.financecollector.controller

import app.properstock.financecollector.model.Market
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/markets")
class MarketController {
    @GetMapping
    fun getAll(): Array<Market> {
        return Market.values()
    }
}