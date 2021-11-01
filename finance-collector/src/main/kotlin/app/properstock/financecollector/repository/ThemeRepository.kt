package app.properstock.financecollector.repository

import app.properstock.financecollector.model.Theme
import org.springframework.data.mongodb.repository.MongoRepository

interface ThemeRepository : MongoRepository<Theme, Long> {
    fun findByName(name: String): Theme?
}