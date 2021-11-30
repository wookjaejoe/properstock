package app.properstock.financecollector.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Ticker(
    @Id
    val id: String? = null,

    /** 마켓 */
    val market: Market,

    /** 종목코드 */
    @Indexed(unique = true)
    var code: String,

    /** 종목명 */
    @Indexed(unique = true)
    var name: String,

    /** 현재가 */
    var price: Int,

    /** 시가총액 */
    var marketCap: Long,

    /** 상장주식수 */
    var shares: Long,

    /** PER */
    var per: Double?,

    /** ROE */
    var roe: Double?,

    /** 외부 링크 */
    var externalLinks: List<ExternalLink> = listOf(),

    /** 산업 */
    var industry: String? = null,

    /** 테마 */
    var themes: List<String> = listOf(),

    /** 마지막 업데이트 시각 */
    var updated: Instant = Instant.now()
)