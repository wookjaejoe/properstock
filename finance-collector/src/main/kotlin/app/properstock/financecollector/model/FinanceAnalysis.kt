package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class FinanceAnalysis(
    @Id
    var code: String,
    var financeStat: FinanceStat,
    @LastModifiedDate
    var timestamp: Instant = Instant.now(),
) {
    data class FinanceStat(
        val currentAssets: TimeSeries<Long> = TimeSeries("유동자산"),
        val currentLiabilities: TimeSeries<Long> = TimeSeries("유동부채"),
        val investmentAssets: TimeSeries<Long> = TimeSeries("투자자산"),
        val nonCurrentLiabilities: TimeSeries<Long> = TimeSeries("비유동부채"),
        val totalAssets: TimeSeries<Long> = TimeSeries("자산총계"),
        val totalDebt: TimeSeries<Long> = TimeSeries("부채총계"),
        val netWorth: TimeSeries<Long> = TimeSeries("순자산"),
    )
}