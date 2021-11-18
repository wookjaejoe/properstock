package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.repository.CorpStatRepository
import app.properstock.financecollector.repository.FinAnalRepository
import app.properstock.financecollector.repository.TickerRepository
import app.properstock.financecollector.service.proper.ProperPriceFormula
import org.springframework.stereotype.Component
import java.time.YearMonth

@Component
class SmartInvestor(
    val corpStatRepository: CorpStatRepository,
    val tickerRepository: TickerRepository,
    val financeAnalRepository: FinAnalRepository

) : ProperPriceFormula {
    override val symbol: String = "SMTINV"
    override val title: String = "Smart Investor"
    override val shortDescription: String = "(사업가치 + 재산가치 - 고정부채) / 발행주식수"
    override val longDescription: String = """
        1. 개요
        야마구치 요헤이(<현명한 초보투자자> 저자)의 기업가치 평가 방법으로 기업의 사업가치, 재산가치, 고정부채를 통해 기업의 가치를 산출하는 방법.
        (사업가치 + 재산가치 - 고정부채) / 발행주식수
        위와 같은 공식을 통해 기업가치를 산출할 수 있다.

        2. 용어설명
        사업가치 : 사업 활동으로 벌어들인 이익을 통해 계산한 기업가치
        재산가치 : 유동자산과 비유동자산(고정자산)을 합한 회사의 재산을 통해 계산한 기업가치
        고정부채 : 1년 이내에 상환하지 않아도 되는 부채
        법인세율 : 법인의 소득금액에 과세 표준대로 부과되는 세금
        기대수익률 : 투자자산을 매입했을 때 얻을 수 있을 것으로 기대되는 수익률
        일드갭(Yield Gap) : 주식의 기대수익률과 확정부이자율의 차이를 나타내는 값
        유동자산 : 결산일로부터 1년 또는 정상영업주기 이내에 현금화가 가능한 자산
        유동부채 : 1년 이내에 상환해야 하는 회사 부채
        유동비율 : 유동자산에서 유동부채를 나눈 값으로 140% 이상이면 안정적이라고 판단
        투자자산 : 비유동자산 중 영업활동과 무관하게 투자를 목적으로 보유하고 있는 회사의 자산
        비유동자산 : 1년 이상 장기간에 걸쳐 현금화할 목적으로 보유하는 자산
        확정부이자율 : 국공채와 같이 위험을 부담하지 않고 받을 수 있는 이자율

        3. 계산식
        (사업가치 + 재산가치 - 고정부채) / 발행주식수
        1) 사업가치 : 영업이익 3년 평균 X ((100% - 법인세율) / 기대수익률)
        		영업이익 : 3년치 영업이익의 평균값 산출 (Y 추정, Y-1, Y-2 영업이익 평균)
                                  * 영업이익은 당해년도 추정치, 1년전, 2년전 값으로 평균을 산출
        		법인세율 : 2021년 기준 대한민국 상장사의 법인세율 25%
        		기대수익률 : 기대수익률은 투자자의 투자 방식에 따라 다르게 적용 가능.
                                 PPST에서는 BBB- 등급 회사채 3년물의 수익률 8.04%를 적용함. 		= 영업이익 3년 평균 X ((100% - 25%) / 8.04%)
                     = 영업이익 3년 평균 X 9.32
        2) 재산가치 : 유동자산 - (유동부채 X 1.2) + 투자자산
                     유동자산 : 회사 재무제표에서 제공
                     유동부채 : 회사 재무제표에서 제공
                      1.2 : 유동부채를 보수적으로 산정하기 위해 곱한 값으로, 
                            상장기업의 유동비율 평균이 1.2배 (출처 : <현명한 초보투자자>)
                     투자자산 : 회사 재무제표에 제공
        3) 고정부채 : 회사 재무제표에서 제공

        4. 데이터 참조
        영업이익 3년치 평균 : 네이버 finance > 종목 > 종목분석 > 영업이익 3년치 평균 계산
        			(https://finance.naver.com/item/coinfo.naver?code=005930)
        BBB- 회사채 3년물 수익률 : 한국 신용평가 > 등급별금리스프레드 > 수익률
                         (https://www.kisrating.co.kr/ratingsStatistics/statics_spread.do)
        유동자산 : 네이버 finance > 종목 > 종목분석 > 재무분석 > 재무상태표 > 당해 추정 유동자산
        유동부채 : 네이버 finance > 종목 > 종목분석 > 재무분석 > 재무상태표 > 당해 추정 유동부채
        투자자산 : 네이버 finance > 종목 > 종목분석 > 재무분석 > 재무상태표 > 당해 추정 투자자산
        고정부채 : 네이버 finance > 종목 > 종목분석 > 투자지표 > 안정성 > 비유동부채비율 > 당해 추정 비유동부채
        총 발행 주식수 : 네이버 finance > 종목 > 상장주식수
        법인세율 : 국세청 법인세율에서 사업연도 소득 3,000억 이상 기업의 법인세율 적용
        (https://www.nts.go.kr/nts/cm/cntnts/cntntsView.do?mi=2372&cntntsId=7746)

        5. 예외사항
        기대수익률에 BBB- 등급 회사채 3년물의 수익률을 적용하는 이유 : 
        1) 주식 투자시 3년 정도는 특정 종목을 보유한다고 가정. 
        2) 주식은 대표적인 위험자산이므로 투기등급인 BBB- 채권 수익률(8.04%)에 준하는 수익률은 기댓값으로 가져가야 안전자산 대비 투자하는 가치가 있음.
        기대수익률의 경우 BBB- 회사채 3년물 수익률 외 일드갭(Yield Gap)을 통해 계산하는 방법도 대안이 될 수 있음.
        Yield Gap(%) = 주식의 기대수익률(%) - 확정부 이자율(%)
                     = KOSPI per의 역수(%) - 3년물 국고채 금리(%)
                     = (1/10) X 100 - 1.69 = 8.31

        6. 요약
        적정주가를 구하기 위해 (사업가치 + 재산가치 - 고정부채) / 발행주식수 X 1억을 계산한다.
        각 계산식은 3.을 참고하고 데이터 출처는 4.를 참고한다.
        full 계산식
        {(영업이익 3년 평균 X ((100% - 법인세율) / 기대수익률)) + (유동자산 - (유동부채 X 1.2) + 투자자산) - 고정부채} / 발행주식수 X 1억
    """.trimIndent()

    override fun calculate(code: String): ProperPriceFormula.Output {
        val corpStat = corpStatRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("기업현황 미확인")
        val thisYear = YearMonth.now().year
        val profitCriteriaYears = 3
        val operatingProfits = corpStat.financeSummary.operatingProfit.data.toSortedMap()
            .filter {
                val ym = it.key
                thisYear - profitCriteriaYears <= ym.year && ym.year <= thisYear
            }
            .map { it.value }

        val operatingProfitAvg = (operatingProfits.filterNotNull().sumOf { it } / profitCriteriaYears).toLong()
        val corporateTaxRate = 25  // 법인새율
        val fixedExpectedReturnRate = 8.31  // 기대수익율

        // 사업가치
        val bussinessValue = operatingProfitAvg * (100 - corporateTaxRate / fixedExpectedReturnRate)

        // 재산가치: 유동자산 - (유동부채 X 1.2) + 투자자산
        val finAnal = financeAnalRepository.findByCode(code) ?: return ProperPriceFormula.Output.dummy("재무분석 미확인")
        val thisYearLastMonth = YearMonth.of(thisYear, 12)
        val currentAsset = finAnal.financeStat.currentAssets.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("유동자산 미확인")
        val currentLiability = finAnal.financeStat.currentLiabilities.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("유동부채 미확인")
        val investmentAsset = finAnal.financeStat.investmentAssets.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("투자자산 미확인")
        val assetValue = (currentAsset - (currentLiability * 1.2) + investmentAsset).toLong()

        // 고정부채
        val nonCurrentLiability =
            finAnal.financeStat.nonCurrentLiabilities.data[thisYearLastMonth] ?: return ProperPriceFormula.Output.dummy("고정부채 미확인")
        // 발행주식수
        val shares = tickerRepository.findByCode(code)?.shares ?: return ProperPriceFormula.Output.dummy("상장주식수 미확인")

        return ProperPriceFormula.Output(
            value = (bussinessValue + assetValue - nonCurrentLiability) / shares,
            note = """
                사업가치: $bussinessValue = 영업이익평균($operatingProfitAvg) * (100 - 법인세율($corporateTaxRate) / 기대수익율($fixedExpectedReturnRate))
                재산가치: $assetValue = 유동자산($currentAsset) - (유동부채($currentLiability) * 1.2) + 투자자산($investmentAsset)
                고정부채: $nonCurrentLiability
                발행주식수: $shares
            """.trimIndent()
        )
    }
}