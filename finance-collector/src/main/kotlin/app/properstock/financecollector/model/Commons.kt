package app.properstock.financecollector.model

import app.properstock.financecollector.exception.KeyValueNotMatchException
import java.time.YearMonth
import java.util.*

fun YearMonth.months() = this.year * 12 + this.monthValue

class TimeSeries<V>(
    val displayName: String,
    val description: String? = null,
    var data: SortedMap<YearMonth, V?> = sortedMapOf()
) {
    fun set(times: List<YearMonth>, data: List<V?>) {
        if (times.size != data.size) throw KeyValueNotMatchException("Length not matched between times and data")
        this.data = times.indices.associate { times[it] to data[it] }.toSortedMap()
    }

    /**
     * @return 가까운 과거 값
     */
    fun nearestFixed(): V? {
        return data.keys
            // 과거 데이터만 필터
            .filter { YearMonth.now().months() - it.months() >= 0 }
            // 가장 최근
            .minByOrNull { YearMonth.now().months() - it.months() }
            .let { if (it == null) null else data[it] }
    }

    /**
     * @return 가까운 추정 값
     */
    fun nearestEstimate(): V? {
        return data.keys
            // 과거 데이터만 필터
            .filter { it.months() - YearMonth.now().months() >= 0 }
            // 가장 최근
            .minByOrNull { it.months() - YearMonth.now().months() }
            .let { if (it == null) null else data[it] }
    }

    fun thisYearLast(): V? = data[data.keys.findLast { ym -> ym.year == YearMonth.now().year }]
}