package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.model.FinanceSummary
import app.properstock.financecollector.model.ProperPriceFormula
import app.properstock.financecollector.repository.CorpStatRepository
import org.springframework.stereotype.Component
import java.text.NumberFormat
import java.util.*
import kotlin.math.floor

@Component
class EpsMultipliedByPer(
    val corpStatRepository: CorpStatRepository
) : ProperPriceFormula {
    override val symbol = "EPSPER"
    override val title = "순이익과 벨류에이션"
    override val shortDescription = "순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한 대중적으로 통용되는 가치평가 방법이다."
    override val longDescription = """
        재무 상 기업가치를 측정하는 가장 기본적인 방법은 기업이 얼마 만큼의 돈을 꾸준하게 벌어왔고, 미래 매출의 연속성이 있는가에 기초한다. 따라서, 기업이 실적을 발표할 때 예상한 이익보다 높거나 낮음에 따라 주가 변동이 일어난다.
        본 공식에서는 기업이 벌고 있는 지표를 나타내는 특정값(EPS)에 시장 참여자들이 꾸준하게 기업을 평가하고 있는 가치(PER)를 곱해 산출한다.
        같은 산업에서 특정 두 회사가 같은 매출을 내더라도 시장 참여자들은 기업의 가치를 다르게 평가 할 수 있다. 예를 들어, 2개 치킨집의 권리금을 생각했을 때 같은 돈을 버는 곳(EPS)이라도 브랜드 가치(PER)에 따라 권리금이 달라 질 것이다.
        본 공식에서는 현재 공개된 기업실적과 시장 참여자들의 평가하는 가치로 적정가치를 표현하며, 기대 가치에 따른 목표주가는 컨세서스 내용을 참고하여 제공한다.
    """.trimIndent()

    override fun calculate(code: String): ProperPriceFormula.Output {
        val corpStat = corpStatRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("기업현황 미확인")
        val epsList = corpStat.financeSummaries[FinanceSummary.Period.YEAR]!!.eps.data.toSortedMap()
        if (!checkSurplus(epsList, 3, 5)) return ProperPriceFormula.Output.dummy("연속 흑자 조건 미충족")
        val perList = corpStat.financeSummaries[FinanceSummary.Period.YEAR]!!.per.data.toSortedMap()
        val per = calculatePerByAvgInSurplus(epsList, perList, 3, 5).round(2)
        if (per.isNaN()) return ProperPriceFormula.Output.dummy("PER 미확인")
        // EPS 계산: 당해년도 EPS
        val eps = corpStat.financeSummaries[FinanceSummary.Period.YEAR]!!.eps.thisYearLast()
            ?: return ProperPriceFormula.Output.dummy("EPS 미확인")

        // 결과 반환
        return ProperPriceFormula.Output(
            value = floor(eps * per),
            arguments = mapOf(
                "당해년도 추정 EPS" to NumberFormat.getNumberInstance(Locale.KOREA).format(eps),
                "추정 PER" to NumberFormat.getNumberInstance(Locale.KOREA).format(per)
            ),
            note = ""
        )
    }
}
