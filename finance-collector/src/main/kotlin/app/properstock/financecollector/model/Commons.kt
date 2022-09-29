package app.properstock.financecollector.model

import app.properstock.financecollector.exception.KeyValueNotMatchException
import java.time.YearMonth
import java.util.*
import kotlin.math.abs

fun YearMonth.months() = this.year * 12 + this.monthValue

class TimeSeries<V>(
    val displayName: String,
    val description: String? = null,
    var data: SortedMap<YearMonth, V?> = sortedMapOf()
) {
    fun set(times: List<YearMonth?>, data: List<V?>) {
        if (times.size != data.size) throw KeyValueNotMatchException("Length not matched between times and data")
        for (i in times.indices) {
            if(times[i] != null && data[i] != null) {
                this.data[times[i]!!] = data[i]
            }
        }
    }

    operator fun get(ym: YearMonth): V? = data[ym]

    fun nearest(): V? {
        return data.filter { it.value != null }
            .minByOrNull { abs(YearMonth.now().months() - it.key.months()) }
            ?.value
    }
}