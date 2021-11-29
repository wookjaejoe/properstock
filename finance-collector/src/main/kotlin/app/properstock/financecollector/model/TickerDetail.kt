package app.properstock.financecollector.model

data class TickerDetail(
    val ticker: Ticker,
    val properPrices: List<ProperPrice>,
    val corpStat: CorpStat?,
    val financeAnalysis: FinanceAnalysis?
)