package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Industry (
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val name: String,

    var marginRate: Double?,

    var tickerCodes: List<String>,

    @LastModifiedDate
    var timestamp: Instant = Instant.now()
)
