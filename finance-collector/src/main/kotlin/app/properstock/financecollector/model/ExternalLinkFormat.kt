package app.properstock.financecollector.model

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

val FN_GUIDE = ExternalLinkFormat(
    domainName = "FnGuide",
    format = "http://comp.fnguide.com/SVO2/ASP/SVD_Main.asp?gicode=A{0}",
    argNames = listOf("code")
)

val ALL_EXTERNAL_LINKS = listOf(NAVER, DART, DAUM, FN_GUIDE)

fun makeExternalLinkSets(code: String): List<ExternalLink> {
    return ALL_EXTERNAL_LINKS.map { it.format(code) }
}