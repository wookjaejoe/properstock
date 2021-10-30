package app.properstock.financecollector.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

@Document
@CompoundIndexes(
    CompoundIndex(name = "ticker_formula", def = "{'tickerCode' : 1, 'formulaSymbol': 1}"),
)
data class ProperPrice(
    val tickerCode: String,
    val formulaSymbol: String,
    var value: Double,
    var note: String? = null
)