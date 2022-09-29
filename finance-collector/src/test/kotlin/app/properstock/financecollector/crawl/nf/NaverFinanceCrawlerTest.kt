package app.properstock.financecollector.crawl.nf

import app.properstock.financecollector.crawl.WebDriverConnector
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class NaverFinanceCrawlerTest @Autowired constructor(
    val naverFinanceCrawler: NaverFinanceCrawler,
    val webDriverConnector: WebDriverConnector,
) {

    @Test
    fun test() {
        webDriverConnector.connect {
            naverFinanceCrawler.crawlCorpStat(this, "034020")
        }
    }
}