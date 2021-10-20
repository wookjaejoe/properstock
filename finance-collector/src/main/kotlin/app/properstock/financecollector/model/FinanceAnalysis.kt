package app.properstock.financecollector.model

import app.properstock.financecollector.crawl.nf.StringMap
import app.properstock.financecollector.exception.KeyValueNotMatchException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.YearMonth

@Document
data class FinanceAnalysis(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val code: String,
    val financeSummary: FinanceSummary,
)

data class FinanceSummary(
    /** 매출액 */
    val sales: TimeSeriesData<Long> = TimeSeriesData("매출액"),
    /** 당기순이익 */
    val netProfit: TimeSeriesData<Long> = TimeSeriesData("당기순이익"),
    /** 영업이익 */
    val operatingProfit: TimeSeriesData<Long> = TimeSeriesData("영업이익"),
    /** ROE */
    val roe: TimeSeriesData<Double> = TimeSeriesData("ROE", "ROE(%)"),
    /** EPS */
    val eps: TimeSeriesData<Long> = TimeSeriesData("EPS", "EPS(원)"),
    /** PER */
    val per: TimeSeriesData<Double> = TimeSeriesData("PER", "PER(배)"),
    /** 발행주식수(보통주) */
    val issuedCommonShares: TimeSeriesData<Long> = TimeSeriesData("발행주식수", "발행주식수(보통주)")
) {
    class TimeSeriesData<V>(
        val displayName: String,
        val description: String? = null,
        val values: MutableMap<YearMonth, V?> = mutableMapOf()
    ) {
        fun set(times: List<YearMonth>, data: List<V?>) {
            if (times.size != data.size) throw KeyValueNotMatchException("Length not matched between times and data")
            for (i in times.indices) {
                values[times[i]] = data[i]
            }
        }
    }
}

val FINANCE_SUMMARY_INDICES = StringMap().apply {
    this["매출액"] = "sales"
    this["영업이익"] = "operatingProfit"
    this["영업이익(발표기준)"] = "-"
    this["세전계속사업이익"] = "profitFromContinuingOperations"
    this["당기순이익"] = "netProfit"
    this["당기순이익(지배)"] = "-"
    this["당기순이익(비지배)"] = "-"
    this["자산총계"] = "totalAssets"
    this["부채총계"] = "totalLiabilities"
    this["자본총계"] = "totalEquity"
    this["자본총계(지배)"] = "-"
    this["자본총계(비지배)"] = "-"
    this["자본금"] = "capitalStock"
    this["영업활동현금흐름"] = "operatinCashFlow"
    this["투자활동현금흐름"] = "investmentActivityCashFlow"
    this["재무활동현금흐름"] = "financialActivityCashFlow"
    this["CAPEX"] = "capex"
    this["FCF"] = "fcf"
    this["이자발생부채"] = "interestBearingLiabilities"
    this["영업이익률"] = "operatingProfitPercentage"
    this["순이익률"] = "netProfitMargin"
    this["ROE(%)"] = "roe"
    this["ROA(%)"] = "roa"
    this["부채비율"] = "debtToEquityRatio"
    this["자본유보율"] = "capitalReserveRatio"
    this["EPS(원)"] = "eps"
    this["PER(배)"] = "per"
    this["BPS(원)"] = "bps"
    this["PBR(배)"] = "pbr"
    this["현금DPS(원)"] = "cashDPS"
    this["현금배당수익률"] = "cashDividendYield"
    this["현금배당성향(%)"] = "cashDividendPayout"
    this["발행주식수(보통주)"] = "issuedCommonShares"
}