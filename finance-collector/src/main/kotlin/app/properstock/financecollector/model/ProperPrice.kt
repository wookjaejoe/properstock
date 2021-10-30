package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
@CompoundIndexes(
    CompoundIndex(
        name = "ticker_formula",
        def = "{'tickerCode' : 1, 'formulaSymbol': 1}",
        unique = true
    ),
)
data class ProperPrice(
    @Id
    val id: String? = null,
    val tickerCode: String,
    val formulaSymbol: String,
    var value: Double,
    var note: String? = null,
    var updated: Instant = Instant.now()
)