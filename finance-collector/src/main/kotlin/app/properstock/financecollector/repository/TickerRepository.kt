package app.properstock.financecollector.repository

import app.properstock.financecollector.model.Ticker
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface TickerRepository : ReactiveMongoRepository<Ticker, Long> {
    fun existsByCode(code: String): Boolean
    fun findByCode(code: String): Mono<Ticker>
}