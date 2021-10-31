package app.properstock.financecollector.crawl.nf

data class NaverFinanceIndustry(
    val name: String,
    val ref: String,
    val marginRate: Double?,
    var tickerRefs: List<String> = listOf()
)