package app.properstock.financecollector.model

import app.properstock.financecollector.exception.KeyValueNotMatchException
import java.time.YearMonth

class TimeSeries<V>(
    val displayName: String,
    val description: String? = null,
    val data: MutableMap<YearMonth, V?> = mutableMapOf()
) {
    fun set(times: List<YearMonth>, data: List<V?>) {
        if (times.size != data.size) throw KeyValueNotMatchException("Length not matched between times and data")
        for (i in times.indices) {
            this.data[times[i]] = data[i]
        }
    }
}