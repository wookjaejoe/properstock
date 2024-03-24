package app.properstock.financecollector.model.fn

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class FnTicker(
    @Id val cd: String,  // 종목코드
    val nm: String,  // 종목명
    val gb: String,
    val mkt_gb: String,
    val stk_gb: String,
)