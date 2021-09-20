package app.properstock.crawl.nf.model

data class Ticker(
    /** 종목명 */
    val name: String,
    /** 현재가 */
    val price: Int,
    /** 시가총액 */
    val marketCap: Long,
    /** 상장주식수 */
    val shares: Int
)