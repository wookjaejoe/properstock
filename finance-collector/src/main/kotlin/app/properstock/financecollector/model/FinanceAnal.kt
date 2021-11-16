package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class FinanceAnal(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    var code: String,
    var financeStat: FinanceStat,
    var updatedAt: Instant = Instant.now()
) {
    data class FinanceStat(
        val currentAssets: TimeSeries<Long> = TimeSeries("유동자산"),
        val currentLiabilities: TimeSeries<Long> = TimeSeries("유동부채"),
        val investmentAssets: TimeSeries<Long> = TimeSeries("투자자산"),
        val nonCurrentLiabilities: TimeSeries<Long> = TimeSeries("비유동부채"),
    )
}