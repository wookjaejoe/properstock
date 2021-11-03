package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.service.proper.ProperPriceFormula
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.*

@Component
class EpsMultipliedByRoe : ProperPriceFormula {
    override val symbol: String = "EPSROE"
    override val title: String = "EPS × ROE"
    override val shortDescription: String = "주가지수는 경제 성장률 + 물가 상승률로서 EPSPER 공식의 추정 PER을 ROE를 통해 적정 PER로 산출하는 방법으로 슈퍼개미 김정환님이 제시하는 만능 공식이다."
    override val longDescription: String = """
        1. 개요
        주가지수는 경제 성장률 + 물가 상승률로서 1번 공식의 추정 PER을 ROE를 통해 적정 PER로 산출하는 방법으로 슈퍼개미 김정환님이 제시하는 만능 공식임.
        
        2. 용어설명
        EPS(Earning per Share) : 기업이 벌어들인 순이익을 총 발행 주식수로 나눈 값이다.
        BPS(Book value per share) 주당 순자산가치, 기업 순자산/발행주식수
        ROE(Return On Equity) : 자본을 이용하여 얼마만큼의 이익을 냈는지를 나타내는 지표
        당기순이익/자본
        BPS(자본), ROE(성장수익)
        ex) 자본총액이 1억인 회사가 1000만원의 당기순이익을 냈다면 ROE 10%가 된다.
        
        3. 계산식
        EPS X ROE = 적정주가
        <ROE = 적정 PER 임을 산출>
        EPS X ROE = 적정주가
        ROE = 적정주가 X 1/EPS
        * EPS = 주당순이익 = 순이익/주식수
        → ROE = 적정주가 X 주식수/순이익
        → ROE = 적정시총 / 순이익 = 적정 PER
        (당해 년도 예상 EPS, ROE 사용)
        
        4. 데이터 참조
        네이버 finance > 종목 > 기업실적 분석 > EPS, ROE 
        
        5. 예외사항
        영업이익이 없어 EPS 및 ROE 가 산출되지 않는 기업의 경우 제외 한다.
        영업이익이 낮고 PER이 고평가 되는 기업의 경우 주가에 비해 상당히 낮은 수치로 반영 될 수 있다.
    """.trimIndent()

    fun calculate(
        epsList: SortedMap<YearMonth, Double?>,
        roeList: SortedMap<YearMonth, Double?>
    ): ProperPriceFormula.Output {
        val thisYear = YearMonth.now().year
        val eps: Double = epsList[epsList.keys.findLast { ym -> ym.year == thisYear }] ?: return ProperPriceFormula.Output.dummy("EPS 미확인")
        val roe: Double = roeList[roeList.keys.findLast { ym -> ym.year == thisYear }] ?: return ProperPriceFormula.Output.dummy("ROE 미확인")
        return ProperPriceFormula.Output(
            value = eps * roe,
            note = """
                당해년도 추정 EPS: $eps
                당해년도 추정 ROE: $roe
            """.trimIndent()
        )
    }
}