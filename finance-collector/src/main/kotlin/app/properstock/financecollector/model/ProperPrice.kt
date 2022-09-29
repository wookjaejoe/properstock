package app.properstock.financecollector.model

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
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
    val tickerCode: String,
    val formulaSymbol: String,
    var value: Double,
    var arguments: Map<String, Any>?,
    var note: String? = null,
    @LastModifiedDate
    var timestamp: Instant = Instant.now()
) {
    data class Dto(
        val tickerCode: String,
        val formulaSymbol: String,
        val value: Double,
        val arguments: Map<String, Any>?,
        val note: String,
        val timestamp: Instant,

        // Additional fields
        val currentPrice: Double,
        val tickerName: String,
        val tickerIndustry: String?,
        val tickerThemes: List<String>,
        val tickerMarket: Market,
        val margin: Double,
        val marginRate: Double,
    )

    companion object {
        @Mapper
        interface ProperPriceMapper {
            fun toDto(
                properPrice: ProperPrice,
                currentPrice: Double,
                tickerName: String,
                tickerIndustry: String?,
                tickerThemes: List<String>,
                tickerMarket: Market,
                margin: Double,
                marginRate: Double,
            ): Dto
        }

        val mapper: ProperPriceMapper = Mappers.getMapper(ProperPriceMapper::class.java)
    }
}