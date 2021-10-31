package app.properstock.financecollector.repository

import app.properstock.financecollector.model.Industry
import org.springframework.data.mongodb.repository.MongoRepository

interface IndustryRepository : MongoRepository<Industry, Long> {
    fun findByName(name: String): Industry?
}