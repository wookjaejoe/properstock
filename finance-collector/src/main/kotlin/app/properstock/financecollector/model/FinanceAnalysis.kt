package app.properstock.financecollector.model

import app.properstock.financecollector.crawl.nf.StringMap
import app.properstock.financecollector.exception.KeyValueNotMatchException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.YearMonth

@Document
data class FinanceAnalysis(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    var code: String,
    var financeSummary: FinanceSummary,
    /** 마지막 업데이트 시각 */
    var updated: Instant = Instant.now()
)

data class FinanceSummary(
    /** 매출액 */
    val sales: TimeSeries<Double> = TimeSeries("매출액"),
    /** 당기순이익 */
    val netProfit: TimeSeries<Double> = TimeSeries("당기순이익"),
    /** 당기순이익(지배) */
    val controllingInterest: TimeSeries<Double> = TimeSeries("당기순이익(지배)"),
    /** 영업이익 */
    val operatingProfit: TimeSeries<Double> = TimeSeries("영업이익"),
    /** ROE */
    val roe: TimeSeries<Double> = TimeSeries("ROE", "ROE(%)"),
    /** EPS */
    val eps: TimeSeries<Double> = TimeSeries("EPS", "EPS(원)"),
    /** PER */
    val per: TimeSeries<Double> = TimeSeries("PER", "PER(배)"),
    /** 발행주식수(보통주) */
    val issuedCommonShares: TimeSeries<Double> = TimeSeries("발행주식수", "발행주식수(보통주)")
) {
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
}
