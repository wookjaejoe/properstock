package app.properstock.financecollector.service.proper.formula

import java.text.NumberFormat
import java.time.YearMonth
import java.util.*

fun Double.round(offset: Int = 0): Double = String.format("%.${offset}f", this).toDouble()
fun Long.formatMillion() = "${NumberFormat.getNumberInstance(Locale.KOREA).format(this / 1_0000_0000)}억"
fun Long.format10Thousand() = "${NumberFormat.getNumberInstance(Locale.KOREA).format(this / 1_0000)}만"

/**
 * 기업의 수익이 연속 흑자인 년도를 리스트로 반환한다.
 * @return 연속 흑자인 년도 리스트
 */
fun surplusYearMonths(
    earningCriteriaList: SortedMap<YearMonth, Long?>,
    minYears: Int,
    maxYears: Int
): List<YearMonth>? {
    val thisYear = YearMonth.now().year

    // 최근 3~5년 흑자 년도 확인
    var surplusYearMonths: List<YearMonth>? = null
    for (i in (minYears..maxYears).reversed()) {
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
    return surplusYearMonths
}

/**
 * 기업의 수익이 연속 흑자인지 여부를 검사한다.
 * @return 연속 흑자일 경우 true, 아닐 경우 false
 */
fun checkSurplus(
    earningCriteriaList: SortedMap<YearMonth, Long?>,
    minYears: Int,
    maxYears: Int
) = !surplusYearMonths(earningCriteriaList, minYears, maxYears).isNullOrEmpty()

/**
 * 순이익이 연속 흑자 조건 만족하면 PER 반환
 * @return 연속 흑자 PER 평균
 */
fun calculatePerByAvgInSurplus(
    earningList: SortedMap<YearMonth, Long?>,
    perList: SortedMap<YearMonth, Double?>,
    minYears: Int,
    maxYears: Int
): Double {
    val surplusYearMonths = surplusYearMonths(earningList, minYears, maxYears)
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
