package app.properstock.financecollector.model.fn

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.YearMonth

@Document
data class FnInvestmentIndicator(
    val code: String,
    val title: String,
    val description: String,
    val yearMonth: YearMonth,
    val value: Double,
    val timestamp: Instant = Instant.now(),
) {
    @Id
    val id = listOf(code, title, yearMonth.toString()).joinToString("_")
}
