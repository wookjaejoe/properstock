package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.service.proper.ProperPriceFormula
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.*

@Component
class EpsMultipliedByPer : ProperPriceFormula {
    override val symbol = "EPSPER"
    override val title = "EPS × PER"
    override val description = "순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한 대중적으로 통용되는 가치평가 방법이다."

    fun calculate(
        epsList: SortedMap<YearMonth, Double?>,
        perList: SortedMap<YearMonth, Double?>
    ): ProperPriceFormula.Output {
        // 연속 흑자 년도 확인: 과거 3년 이상 연속 흑자 기록된 년도 리스트 확인
        val thisYear = YearMonth.now().year

        // 최근 3~5년 흑자 년도 확인
        var surplusYearMonths: List<YearMonth>? = null
        for (i in (3..5).reversed()) {
            surplusYearMonths = epsList
                .filter { entry ->
                    val ym = entry.key
                    thisYear - i <= ym.year && ym.year <= thisYear - 1
                }
                .takeIf {
                    // 모두 양수일 경우
                    it.values.all { eps -> eps != null && eps > 0 }
                }
                ?.keys?.toList()

            if (surplusYearMonths != null) {
                break
            }
        }

        // 연속 흑자 조건 충족하는지 확인
        if(surplusYearMonths.isNullOrEmpty()) return ProperPriceFormula.Output.dummy("연속 흑자 조건 미충족")

        // PER 계산
        val whitePerList: Collection<Double>? = perList
            .filterKeys { surplusYearMonths.contains(it) }
            .values
            .takeIf { it.all { v -> v != null } }
            ?.map { it!! }

        // PER 계산
        if(whitePerList.isNullOrEmpty()) return ProperPriceFormula.Output.dummy("PER 미확인")
        val per = whitePerList.sumOf { it } / surplusYearMonths.size

        // EPS 계산: 당해년도 EPS
        val eps: Double = epsList[epsList.keys.findLast { ym -> ym.year == thisYear }] ?: return ProperPriceFormula.Output.dummy("EPS 미확인")

        // 결과 반환
        return ProperPriceFormula.Output(
            value = eps * per,
            note = "EPS: $eps, PER: $per"
        )
    }
}
