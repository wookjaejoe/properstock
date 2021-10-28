package app.properstock.financecollector.service.proper

import org.springframework.stereotype.Service

@Service
class ProperPriceCalculator {
}

data class ProperPrice(
    val formulaArguments: List<Argument>,
    val value: Double
) {
    data class Argument(
        val name: String,
        val value: Double
    )
}

interface ProperPriceFormula {
    val id: String
    val title: String
    val description: String

    fun calculate(): ProperPrice
}

class EpsMultipliedByPER(
    private val eps: Double,
    private val per: Double
) : ProperPriceFormula {
    override val id = "EPSXPER"
    override val title = "EPS × PER"
    override val description = "순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한 대중적으로 통용되는 가치평가 방법이다."

    override fun calculate(): ProperPrice = ProperPrice(
        formulaArguments = listOf(
            ProperPrice.Argument("EPS", eps),
            ProperPrice.Argument("PER", per)
        ),
        value = eps * per
    )
}
