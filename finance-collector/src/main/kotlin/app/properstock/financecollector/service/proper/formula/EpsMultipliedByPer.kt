package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.service.proper.ProperPriceFormula
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.*
import kotlin.math.floor

@Component
class EpsMultipliedByPer : ProperPriceFormula {
    override val symbol = "EPSPER"
    override val title = "EPS × PER"
    override val shortDescription = "순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한 대중적으로 통용되는 가치평가 방법이다."
    override val longDescription = """
        1. 개요
        순이익에 밸류에이션을 곱해서 기업의 주가를 계산하는 방식으로, 기업의 이익에 기초한 대중적으로 통용되는 가치평가 방법이다.

        2. 용어 설명
        EPS(Earning per Share) : 기업이 벌어들인 순이익을 총 발행 주식수로 나눈 값이다.
        PER (Price Earning Ratio) : 기업의 순수익 대비 주가 수준이 얼마나 되는지를 나타내는 지표로 수치가 낮을수록 저평가, 높을수록 고평가 되었다고 볼 수 있다.

        3. 계산
        EPS 추정치 산출 : 순이익 / 총 발행 주식수
        PER 추정치 산출 : 업종 평균 PER, KOSPI 평균 PER, 섹터 평균 PER 등 다양한 방법으로 산출 가능하다. (PPST에서는 개별 기업의 과거 5년 평균 PER 산출)

        4. 예상 EPS, PER 데이터 참조
        i. 예상 EPS : 네이버 finance > 종목 > 종목분석 > 연도별 EPS 컨센서스
        당해년도 예상 EPS 컨센서스 값을 적용한다.
            ex) 2021년 10월 16일 적정주가 계산할 경우 : 2021/12(E) EPS 예상값을 적용
                2022년 02월 23일 적정주가 계산할 경우 : 2022/12(E) EPS 예상값을 적용
        			(https://finance.naver.com/item/coinfo.naver?code=005930)
        ※ 해당 순이익 값은 FnGuide에서 제공하는 애널리스트 컨센서스 값을 기초로 한다.
        ii. PER : 개별 기업의 과거 5년 평균 PER로 계산
        PER : NAVER Finance > 종목 > 종목분석 > PER (과거 5년 평균 PER의 평균)
        

        5. 예외사항
        i. EPS X PER 공식은 과거 5년 중 3년 이상 이익이 흑자인 경우에 대해서만 적용
        ii. 5년 동안 회사의 수익이 없어서 EPS 값이 음수일 경우 : 해당 적정주가 공식에 부합하지 않는 종목이라 판단하고 취급 제외
        iii. 5년 중 특정 년도에 적자가 나서 EPS 값이 음수일 경우 : 
        Y-5 적자일 경우, Y-4 ~ Y-1 4년간 평균 PER 값 
        Y-5, Y-4 적자일 경우, Y-3 ~ Y-1 3년간 평균 PER 값
        Y-4 적자일 경우 Y-5, Y-3 ~ Y-1 4년간 평균 PER 값

        6. 요약
        EPS : NAVER Finance에서 순이익 추정치 값 가져오기.
        PER : NAVER Finance에서 개별 기업의 과거 5년치 평균 PER로 계산하기(예외사항 적용 必)
    """.trimIndent()

    fun calculate(
        epsList: SortedMap<YearMonth, Double?>,
        perList: SortedMap<YearMonth, Double?>
    ): ProperPriceFormula.Output {
        val per = calculatePerByAvg(epsList, perList).run { floor(this) }
        if (per.isNaN()) return ProperPriceFormula.Output.dummy("PER 미확인")

        val thisYear = YearMonth.now().year
        // EPS 계산: 당해년도 EPS
        val eps: Double =
            epsList[epsList.keys.findLast { ym -> ym.year == thisYear }]?.run { floor(this) }
                ?: return ProperPriceFormula.Output.dummy("EPS 미확인")

        // 결과 반환
        return ProperPriceFormula.Output(
            value = eps * per,
            note = """
                당해년도 추정 EPS: $eps
                최근 5년 내 연속 흑자 PER 평균: $per
            """.trimIndent()
        )
    }
}
