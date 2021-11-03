package app.properstock.financecollector.repository

import app.properstock.financecollector.model.Ticker
import org.springframework.data.mongodb.repository.MongoRepository

interface TickerRepository : MongoRepository<Ticker, String> {
    fun findByCode(code: String): Ticker?
}