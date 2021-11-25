package app.properstock.financecollector.model

import app.properstock.financecollector.TZ_KR
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class ExternalLinkFormat(
    val domainName: String,
    val format: String,
    val argNames: List<String>
) {
    fun format(vararg args: String): ExternalLink {
        var result = format
        for (i in args.indices) {
            result = result.replace("{$i}", args[i])
        }
        return ExternalLink(
            domainName,
            result
        )
    }
}

data class ExternalLink(
    val domainName: String,
    val url: String
)

val NAVER = ExternalLinkFormat(
    domainName = "Naver",
    format = "https://finance.naver.com/item/main.nhn?code={0}",
    argNames = listOf("code")
)

val DART = ExternalLinkFormat(
    domainName = "Dart",
    format = "https://dart.fss.or.kr/html/search/SearchCompany_M2.html?textCrpNM={0}",
    argNames = listOf("code")
)

val DAUM = ExternalLinkFormat(
    domainName = "Daum",
    format = "https://finance.daum.net/quotes/A{0}",
    argNames = listOf("code")
)

val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private fun startDate() = ZonedDateTime.now(ZoneId.of(TZ_KR)).minusYears(1).format(DATE_FORMAT)
private fun endDate() = ZonedDateTime.now(ZoneId.of(TZ_KR)).format(DATE_FORMAT)

val HANKYUNG = ExternalLinkFormat(
    domainName = "Hankyung",
    format = "http://consensus.hankyung.com/apps.analysis/analysis.list?sdate=${startDate()}&edate=${endDate()}&now_page=1&pagenum=20&search_text={0}",
    argNames = listOf("code")
)

val ALL_EXTERNAL_LINKS = listOf(NAVER, DART, DAUM, HANKYUNG)

fun makeExternalLinkSets(code: String): List<ExternalLink> {
    return ALL_EXTERNAL_LINKS.map { it.format(code) }
}