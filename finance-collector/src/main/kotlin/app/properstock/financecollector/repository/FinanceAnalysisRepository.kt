package app.properstock.financecollector.repository

import app.properstock.financecollector.model.FinanceAnalysis
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface FinanceAnalysisRepository : ReactiveMongoRepository<FinanceAnalysis, Long> {
    fun findByCode(code: String): Mono<FinanceAnalysis>
}