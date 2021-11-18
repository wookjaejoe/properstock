package app.properstock.financecollector.service.proper.formula

import java.text.NumberFormat
import java.time.YearMonth
import java.util.*

fun Double.round(offset: Int = 0): Double = String.format("%.${offset}f", this).toDouble()
fun Long.formatMillion() = "${NumberFormat.getNumberInstance(Locale.KOREA).format(this / 1_0000_0000)}억"
fun Long.format10Thousand() = "${NumberFormat.getNumberInstance(Locale.KOREA).format(this / 1_0000)}만"

/**
 * 순이익이 연속 흑자 조건 만족하면 PER 반환
 */
fun calculatePerByAvg(
    earningCriteriaList: SortedMap<YearMonth, Long?>,
    perList: SortedMap<YearMonth, Double?>
): Double {
    // 연속 흑자 년도 확인: 과거 3년 이상 연속 흑자 기록된 년도 리스트 확인
    val thisYear = YearMonth.now().year

    // 최근 3~5년 흑자 년도 확인
    var surplusYearMonths: List<YearMonth>? = null
    for (i in (3..5).reversed()) {
        surplusYearMonths = earningCriteriaList
            .filter { entry ->
                val ym = entry.key
                thisYear - i <= ym.year && ym.year <= thisYear - 1
            }
            .takeIf {
                // 모두 양수일 경우
                it.values.all { earning -> earning != null && earning > 0 }
            }
            ?.keys?.toList()

        if (surplusYearMonths != null) {
            break
        }
    }

    // 연속 흑자 조건 충족하는지 확인
    if (surplusYearMonths.isNullOrEmpty()) return Double.NaN

    // PER 계산
    val whitePerList: Collection<Double>? = perList
        .filterKeys { surplusYearMonths.contains(it) }
        .values
        .takeIf { it.all { v -> v != null } }
        ?.map { it!! }

    // PER 계산
    if (whitePerList.isNullOrEmpty()) return Double.NaN
    return whitePerList.sumOf { it } / surplusYearMonths.size
}


