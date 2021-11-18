package app.properstock.financecollector.repository

import app.properstock.financecollector.model.FinanceAnal
import org.springframework.data.mongodb.repository.MongoRepository

interface FinAnalRepository : MongoRepository<FinanceAnal, Long> {
    fun findByCode(code: String): FinanceAnal?
}