package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class CorpStat(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    var code: String,
    var financeSummaries: Map<FinanceSummary.Period, FinanceSummary>,  // fixme: 리스트로 바꾸자
    /** 마지막 업데이트 시각 */
    var updated: Instant = Instant.now()
)

data class FinanceSummary(
    val period: Period,
    /** 매출액 */
    val sales: TimeSeries<Long> = TimeSeries("매출액"),
    /** 당기순이익 */
    val netProfit: TimeSeries<Long> = TimeSeries("당기순이익"),
    /** 당기순이익(지배) */
    val controllingInterest: TimeSeries<Long> = TimeSeries("당기순이익(지배)"),
    /** 영업이익 */
    val operatingProfit: TimeSeries<Long> = TimeSeries("영업이익"),
    /** ROE */
    val roe: TimeSeries<Double> = TimeSeries("ROE", "ROE(%)"),
    /** EPS */
    val eps: TimeSeries<Long> = TimeSeries("EPS", "EPS(원)"),
    /** PER */
    val per: TimeSeries<Double> = TimeSeries("PER", "PER(배)"),
    /** 발행주식수(보통주) */
    val issuedCommonShares: TimeSeries<Long> = TimeSeries("발행주식수", "발행주식수(보통주)")
) {
    enum class Period {
        YEAR,
        QUARTER
    }
}