package app.properstock.financecollector.repository

import app.properstock.financecollector.model.FinanceAnalysis
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface FinanceAnalysisRepository : ReactiveMongoRepository<FinanceAnalysis, Long> {
}