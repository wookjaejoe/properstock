package app.properstock.financecollector.model

import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Component

@Component
interface ProperPriceFormula {
    val symbol: String
    val title: String
    val shortDescription: String
    val longDescription: String

    fun calculate(code: String): Output

    class Output(
        val value: Double,
        val arguments: Map<String, Any>,
        val note: String? = null
    ) {
        companion object {
            fun dummy(
                arguments: Map<String, Any>,
                note: String
            ) = Output(
                Double.NaN,
                arguments = arguments,
                note
            )

            fun dummy(note: String) = dummy(mapOf(), note)
        }
    }

    data class Dto(
        val symbol: String,
        val title: String,
        val shortDescription: String,
        val longDescription: String
    )

    companion object {
        @Mapper
        interface ProperPriceFormulaMapper {
            fun toDto(properPriceFormula: ProperPriceFormula): Dto
        }

        val mapper: ProperPriceFormulaMapper = Mappers.getMapper(ProperPriceFormulaMapper::class.java)
    }
}
