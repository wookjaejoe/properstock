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
    val name: String,

    /** 현재가 */
    val price: Int,

    /** 시가총액 */
    val marketCap: Long,

    /** 상장주식수 */
    val shares: Int,

    /** 링크 */
    val link: String,

    /** 산업 */
    val industry: String? = null,

    /** 테마 */
    val themes: List<String> = emptyList(),

    /** 마지막 업데이트 시각 */
    var updated: Instant = Instant.now()
)
