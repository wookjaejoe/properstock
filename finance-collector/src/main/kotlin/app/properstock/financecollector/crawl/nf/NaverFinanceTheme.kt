package app.properstock.financecollector.crawl.nf

data class NaverFinanceTheme(
    val name: String,
    val ref: String,
    val marginRate: Double?,
    var tickerRefs: List<String> = listOf()
)