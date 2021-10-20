package app.properstock.financecollector.repository

import app.properstock.financecollector.model.Ticker
import org.springframework.data.mongodb.repository.MongoRepository

interface TickerRepository : MongoRepository<Ticker, String> {
    fun existsByCode(code: String): Boolean
    fun findByCode(code: String): Ticker?
    fun deleteByCode(code: String)
}