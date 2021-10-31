package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Industry (
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val name: String,

    var tickerCodes: List<String>
)
