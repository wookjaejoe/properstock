package app.properstock.financecollector.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class DatabaseSequence(
    val id: String,
    var value: Long
)