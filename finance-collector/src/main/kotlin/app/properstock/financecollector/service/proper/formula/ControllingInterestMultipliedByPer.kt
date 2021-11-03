package app.properstock.financecollector.service.proper.formula

import app.properstock.financecollector.service.proper.ProperPriceFormula
import org.springframework.stereotype.Component
import java.time.YearMonth
import java.util.*

@Component
class ControllingInterestMultipliedByPer : ProperPriceFormula {
    override val symbol = "CTRINTPER"
    override val title = "지배주주순이익 x PER"
    override val shortDescription =
        "지배주주순이익은 모회사(지배기업) 순이익에 자회사(관계기업, 종속기업) 순이익 지분 만큼을 합산하여 적용한 순이익이다. 지배주주순이익 계산을 통해 자회사의 가치까지 합산한 기업의 순이익을 구할 수 있다. 지배주주순이익을 구하는 방법은 관계기업, 종속기업 각각의 순이익에 모회사 지분율을 곱한 뒤 합산하여 구한다. 이를 통해 IFRS 회계법으로 표현되지 않는 지배주주순이익을 구할 수 있다는 장점이 있다."
    override val longDescription = """
        1.	개요
        지배주주순이익은 모회사(지배기업) 순이익에 자회사(관계기업, 종속기업) 순이익 지분 만큼을 합산하여 적용한 순이익이다. 지배주주순이익 계산을 통해 자회사의 가치까지 합산한 기업의 순이익을 구할 수 있다. 
        지배주주순이익을 구하는 방법은 관계기업, 종속기업 각각의 순이익에 모회사 지분율을 곱한 뒤 합산하여 구한다. 이를 통해 IFRS 회계법으로 표현되지 않는 지배주주순이익을 구할 수 있다는 장점이 있다.
        * 수식어 없이 단순히 ‘순이익’이라고 할 경우 IFRS 회계법을 적용한 순이익을 지칭함.
        * IFRS 회계법 상 순이익 계산 : 모회사(지배기업) 순이익 + 자회사(종속기업) 순이익

        2.	용어 설명
        IFRS(International Financial Reporting Standards) : 회계 처리와 재무제표에 대한 국제적 통일성을 높이기 위해 국제회계기준위원회에서 마련해 공표하는 회계기준. IFRS 회계법상 종속기업은 모회사에 기업 전체를 지배 당하는 것으로 판단하여 모회사 순이익에 종속기업의 순이익을 단순 합산함.
        관계기업 : 모회사의 지분율이 20% 이상 ~ 50% 이하인 기업
        종속기업 : 모회사의 지분율이 50% 초과 ~ 100%인 기업
        지배주주순이익 : 모회사(지배기업)의 순이익에 지분율 만큼의 자회사(종속기업, 관계기업) 순이익을 더한 기업의 이익.
        순이익 : IFRS 회계법상의 순이익. 보통 순이익이라 함은 IFRS 기준의 순이익을 지칭함.
        * IFRS 회계법 상 순이익 계산 : 모회사(지배기업) 순이익 + 자회사(종속기업) 순이익
        PER (Price Earning Ratio) : 기업의 순수익 대비 주가 수준이 얼마나 되는지를 나타내는 지표로 수치 가 낮을수록 저평가, 높을수록 고평가 되었다고 볼 수 있다.

        3.	계산
        지배주주순이익 x PER / 발행주식수 : {지배기업 순이익 + (관계기업 순이익 x 지분율) + (종속회사 순이익 x 지분율)} x PER / 발행주식수
        순이익 x PER / 발행주식수 : 지배기업 순이익 + 종속기업 순이익 / 발행주식수
        * 각 순이익은 당해년도 추정치를 적용
        PER 추정치 산출 : 업종 평균 PER, KOSPI 평균 PER, 섹터 평균 PER 등 다양한 방법으로 산출 가능하다. (PPST에서는 개별 기업의 과거 5년 평균 PER 산출)

        4.	지배주주이익, 순이익, PER 데이터 참조
        i. 지배주주순이익 : 네이버 finance > 종목 > 종목분석 > 당기순이익(지배)
                           * 당기순이익(지배)는 당해년도 추정치 적용
        ii. 순이익 : 네이버 finance > 종목 > 종목분석 > 당기순이익
                           * 당기순이익은 당해년도 추정치 적용
        iii. PER : 개별 기업의 과거 5년 평균 PER로 계산
        PER : NAVER Finance > 종목 > 종목분석 > PER (과거 5년 평균 PER의 평균)


        5. 예외사항
        지배주주순이익, 순이익에 PER를 곱한 값은 적정 시가총액이므로 발행주식수로 나누어 적정주가를 구해야 한다.

        6. 요약
        IFRS 회계법을 몰라도 지배주주순이익, 순이익을 구별할 수 있다. NAVER Finance에 제공하기 때문이다. 
        지배주주순이익 : 네이버 finance > 종목 > 종목분석 > 당기순이익(지배)
        순이익 : 네이버 finance > 종목 > 종목분석 > 당기순이익
        지배주주순이익 x PER / 발행주식수
        순이익 x PER / 발행주식수
    """.trimIndent()

    fun calculate(
        controllingInterestList: SortedMap<YearMonth, Double?>,
        perList: SortedMap<YearMonth, Double?>
    ): ProperPriceFormula.Output {
        val per = calculatePerByAvg(controllingInterestList, perList)
        if (per.isNaN()) return ProperPriceFormula.Output.dummy("PER 미확인")

        val thisYear = YearMonth.now().year
        // 지배주주순이익 계산: 당해년도 지배주주순이익
        val controllingInterest: Double = controllingInterestList[controllingInterestList
            .keys
            .findLast { ym -> ym.year == thisYear }]
            ?: return ProperPriceFormula.Output.dummy("당해년도 지배주순이익 미확인")
        return ProperPriceFormula.Output(
            per * controllingInterest,
            """
                당해년도 추정 지배주주순이익: $controllingInterest
                최근 5년 내 연속 흑자 PER 평균: $per
            """.trimIndent()
        )
    }
}
