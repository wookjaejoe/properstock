package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.model.FinanceSummary
import app.properstock.financecollector.model.ProperPriceFormula
import app.properstock.financecollector.repository.CorpStatRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.util.isCommonStock
import org.springframework.stereotype.Component
import java.time.YearMonth

@Component
class ControllingInterestMultipliedByPer(
    epsMultipliedByPer: EpsMultipliedByPer,
    val corpStatRepository: CorpStatRepository,
    val tickerRepository: TickerRepository
) : ProperPriceFormula {
    override val symbol = "CTRINTPER"
    override val title = "지배주주순이익과 벨류에이션"
    override val shortDescription = "기업의 자회사 이익이 해당 기업 주가에 미치는 영향을 고려하여, 지배주주순이익과 PER 곱으로 적정주가를 산출한다."
    override val longDescription = """
        "${epsMultipliedByPer.title}" 공식에서는 순이익을 기업의 가치로 삼고, 선형 변환의 계수로 PER을 사용하여 적정주가를 산출하였다. 본 공식은 순이익 대신 지배주주순이익을 사용함으로써, 자회사의 가치를 고려한 적정주가를 산출한다.
        지배주주순이익은 모회사(지배기업) 순이익에 자회사(관계기업, 종속기업) 순이익 지분 만큼을 합산하여 계산된다. 이를 통해 IFRS 회계법으로 표현되지 않는 지배주주순이익을 구할 수 있다는 장점이 있다.
        * 수식어 없이 단순히 ‘순이익’이라고 할 경우 IFRS 회계법을 적용한 순이익을 지칭한다.
        * IFRS 회계법 상 순이익 계산 : 모회사(지배기업) 순이익 + 자회사(종속기업) 순이익
    """.trimIndent()

    override fun calculate(code: String): ProperPriceFormula.Output {
        if (!isCommonStock(code)) return ProperPriceFormula.Output.dummy("미취급(본 공식은 보통주에 대해서만 적용 가능)")
        val corpStat = corpStatRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("기업현황 미확인")
        val controllingInterestList = corpStat.financeSummaries[FinanceSummary.Period.YEAR]!!.controllingInterest.data.toSortedMap()
        if (!checkSurplus(controllingInterestList, 3, 5)) return ProperPriceFormula.Output.dummy("연속 흑자 조건 미충족")
        val perList = corpStat.financeSummaries[FinanceSummary.Period.YEAR]!!.per.data.toSortedMap()
        val per = calculatePerByAvgInSurplus(controllingInterestList, perList, 3, 5).round(2)
        if (per.isNaN()) return ProperPriceFormula.Output.dummy("PER 미확인")
        val thisYear = YearMonth.now().year
        // 지배주주순이익 계산: 당해년도
        val controllingInterest = controllingInterestList[
            controllingInterestList
                .keys
                .findLast { ym -> ym.year == thisYear }
        ] ?: return ProperPriceFormula.Output.dummy("당해년도 지배주순이익 미확인")

        // 상장주식수
        val issued = tickerRepository.findByCode(code)?.shares ?: return ProperPriceFormula.Output.dummy("당해년도 발행주식수 미확인")
        return ProperPriceFormula.Output(
            value = (per * controllingInterest / issued).round(),
            arguments = mapOf(
                "당해년도 추정 지배주주순이익" to controllingInterest.formatMillion(),
                "추정 PER" to per,
                "발행주식수" to issued.toLong().format10Thousand()
            ),
            note = ""
        )
    }
}
