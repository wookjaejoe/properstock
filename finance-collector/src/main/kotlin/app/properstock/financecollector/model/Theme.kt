package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Theme(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val name: String,

    var marginRate: Double?,

    var tickerCodes: List<String>,

    var updatedAt: Instant = Instant.now()
)