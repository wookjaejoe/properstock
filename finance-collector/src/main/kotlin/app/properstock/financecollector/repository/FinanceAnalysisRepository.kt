package app.properstock.financecollector.repository

import app.properstock.financecollector.model.FinanceAnalysis
import org.springframework.data.mongodb.repository.MongoRepository

interface FinanceAnalysisRepository : MongoRepository<FinanceAnalysis, Long> {
    fun findByCode(code: String): FinanceAnalysis?
}