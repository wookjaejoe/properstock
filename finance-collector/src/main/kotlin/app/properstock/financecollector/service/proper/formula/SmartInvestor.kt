package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.model.FinanceSummary
import app.properstock.financecollector.model.ProperPriceFormula
import app.properstock.financecollector.repository.CorpStatRepository
import app.properstock.financecollector.repository.FinanceAnalRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.util.isCommonStock
import org.springframework.stereotype.Component
import java.time.YearMonth

@Component
class SmartInvestor(
    val corpStatRepository: CorpStatRepository,
    val tickerRepository: TickerRepository,
    val financeAnalRepository: FinanceAnalRepository
) : ProperPriceFormula {
    override val symbol: String = "SMTINV"
    override val title: String = "현명한 투자자"
    override val shortDescription: String = "야마구치 요헤이(<현명한 초보투자자> 저자)의 기업가치 평가 방법으로 기업의 사업가치, 재산가치, 고정부채를 통해 기업의 가치를 산출하는 방법"
    override val longDescription: String = """
        <현명한 초보투자자> 저자 야마구치 요헤이의 기업가치 평가 방법으로 기업의 사업가치, 재산가치, 고정부채를 통해 기업의 가치를 산출하는 방법이다.
        사업가치, 재산가치, 고정부채를 기반하여 기업의 가치를 구하고, 최종적으로 발행주식수를 나누어 적정 주가를 산출한다.
    """.trimIndent()

    override fun calculate(code: String): ProperPriceFormula.Output {
        if (!isCommonStock(code)) return ProperPriceFormula.Output.dummy("미취급(본 공식은 보통주에 대해서만 적용 가능)")
        val corpStat = corpStatRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("기업현황 미확인")
        val thisYear = YearMonth.now().year
        val profitCriteriaYears = 3
        val operatingProfitMap = corpStat.financeSummaries[FinanceSummary.Period.YEAR]!!.operatingProfit.data.toSortedMap()
            .filter {
                val ym = it.key
                thisYear - profitCriteriaYears + 1 <= ym.year && ym.year <= thisYear
            }
        val operatingProfits = operatingProfitMap
            .map { it.value }
            .filterNotNull()

        if (operatingProfits.isEmpty() || operatingProfits.sumOf { it } == 0L) return ProperPriceFormula.Output.dummy("영업이익 미확인")
        val operatingProfitAvg = (operatingProfits.sumOf { it } / operatingProfits.size)
        val corporateTaxRate = 25  // 법인세율
        val fixedExpectedReturnRate = 8.31  // 기대수익율

        // 사업가치
        val businessValue = (operatingProfitAvg * ((100 - corporateTaxRate) / fixedExpectedReturnRate)).toLong()

        // 재산가치: 유동자산 - (유동부채 X 1.2) + 투자자산
        val finAnal = financeAnalRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("재무분석 미확인")
        val thisYearLastMonth = YearMonth.of(thisYear, 12)
        val currentAsset = finAnal.financeStat.currentAssets.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("유동자산 미확인")
        val currentLiability = finAnal.financeStat.currentLiabilities.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("유동부채 미확인")
        val investmentAsset =
            finAnal.financeStat.investmentAssets.data[YearMonth.of(thisYear - 1, 12)] ?: return ProperPriceFormula.Output.dummy("투자자산 미확인")
        val assetValue = (currentAsset - (currentLiability * 1.2) + investmentAsset).toLong()

        // 고정부채
        val nonCurrentLiability =
            finAnal.financeStat.nonCurrentLiabilities.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("고정부채 미확인")
        // 발행주식수
        val shares = tickerRepository.findByCode(code)?.shares ?: return ProperPriceFormula.Output.dummy("상장주식수 미확인")
        return ProperPriceFormula.Output(
            value = ((businessValue + assetValue - nonCurrentLiability) / shares).toDouble(),
            arguments = mapOf(
                "영업이익" to operatingProfitMap,
                "영업이익평균" to operatingProfitAvg,
                "법인세율" to corporateTaxRate,
                "기대수익율" to fixedExpectedReturnRate.round(2),
                "사업가치" to businessValue,
                "유동자산" to currentAsset,
                "유동부채" to currentLiability,
                "투자자산" to investmentAsset,
                "재산가치" to assetValue,
                "고정부채" to nonCurrentLiability,
                "발행주식수" to shares
            ),
            note = ""
        )
    }
}