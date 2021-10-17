package app.properstock.financecollector.controller

import app.properstock.financecollector.model.Ticker
import app.properstock.financecollector.repository.TickerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/tickers")
class TickerController(
    val tickerRepository: TickerRepository
) {
    @GetMapping
    fun getTickers(): Flux<Ticker> {
        return tickerRepository.findAll()
    }

    @GetMapping("/{code}")
    fun getTicker(@PathVariable code: String): Mono<Ticker> {
        return tickerRepository.findByCode(code)
    }
}