package app.properstock.financecollector.repository

import app.properstock.financecollector.model.CorpStat
import org.springframework.data.mongodb.repository.MongoRepository

interface CorpStatRepository : MongoRepository<CorpStat, Long> {
    fun findByCode(code: String): CorpStat?
}