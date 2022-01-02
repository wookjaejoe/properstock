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
    fun set(times: List<YearMonth>, data: List<V?>) {
        if (times.size != data.size) throw KeyValueNotMatchException("Length not matched between times and data")
        this.data = times.indices.associate { times[it] to data[it] }.toSortedMap()
    }

    fun nearest(): V? {
        return data.filter { it.value != null }
            .minByOrNull { abs(YearMonth.now().months() - it.key.months()) }
            ?.value
    }
}