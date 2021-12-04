package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.model.CorpStat
import app.properstock.financecollector.model.ProperPriceFormula
import app.properstock.financecollector.repository.CorpStatRepository
import app.properstock.financecollector.repository.TickerRepository
import org.springframework.stereotype.Component
import kotlin.math.floor

@Component
class EpsMultipliedByRoe(
    val corpStatRepository: CorpStatRepository,
    val tickerRepository: TickerRepository,
    epsMultipliedByPer: EpsMultipliedByPer
) : ProperPriceFormula {
    override val symbol: String = "EPSROE"
    override val title: String = "순이익과 성장성"
    override val shortDescription: String =
        """주가지수는 경제 성장률 + 물가 상승률로서 "${epsMultipliedByPer.title}" 공식의 추정 PER을 ROE를 통해 적정 PER로 산출하는 방법으로 슈퍼개미 김정환님이 제시하는 만능 공식이다."""
    override val longDescription: String = """
        재무 상 기업가치를 측정하는 가장 기본적인 방법은 기업이 얼마 만큼의 돈을 꾸준하게 벌고 있고, 미래에 매출의 연속성이 있는가에 기초한다. 따라서, 기업이 실적을 발표할 때 예상한 이익보다 높거나 낮음에 따라 주가 변동이 일어난다.
        해당 공식에서는 기업이 벌고 있는 지표를 나타내는 특정값(EPS)에 자산대비 얼만큼 효율적으로 돈을 벌고 있는 지를 나타내는 지표인 ROE를 곱해 적정가치를 산출한다.
        EX) 2000만원 자본을 가지고 200만원을 벌 경우 ROE 10%, 4000만원 자본을 가지고 200만원을 별 경우 ROE 5%
    """.trimIndent()

    override fun calculate(code: String): ProperPriceFormula.Output {
        val corpStat = corpStatRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("기업현황 미확인")
        val yearlyEps = corpStat.financeSummaries[CorpStat.FinanceSummary.Period.YEAR]!!.eps.data
        if (!checkSurplus(yearlyEps, 3, 5))
            return ProperPriceFormula.Output.dummy(
                arguments = mapOf("연도별 EPS" to yearlyEps),
                note = "연속 흑자 조건 미충족"
            )
        val ticker = tickerRepository.findByCode(code)
        val eps = corpStat.financeSummaries[CorpStat.FinanceSummary.Period.YEAR]!!.eps.nearestFixed()
            ?: return ProperPriceFormula.Output.dummy("EPS 미확인")
        val roe = ticker?.roe
        if (roe == null || roe.isNaN()) {
            return ProperPriceFormula.Output.dummy("ROE 미확인")
        }
        return ProperPriceFormula.Output(
            value = floor(eps * roe),
            arguments = mapOf(
                "추정 EPS" to eps,
                "추정 ROE" to roe.round(2)
            ),
            note = ""
        )
    }
}