package app.properstock.financecollector.service.proper

import org.springframework.stereotype.Service

@Service
class ProperPriceCalculator {
}

interface ProperPriceFormula {
    val id: String
    val title: String
    val description: String
    val arguments: List<Argument<Any>>

    fun calculate(): Double

    data class Argument<T>(
        val name: String,
        val value: T
    )
}

class EpsMultipliedByPer(
    val eps: Double,
    val per: Double
) : ProperPriceFormula {
    override val id = "EPS_PER"
    override val title = "EPS × PER"
    override val description = ""
    override val arguments: List<ProperPriceFormula.Argument<Any>> = listOf(
        ProperPriceFormula.Argument("EPS", eps),
        ProperPriceFormula.Argument("PER", per)
    )

    override fun calculate(): Double {
        return eps * per
    }
}
