package app.properstock.financecollector

import app.properstock.financecollector.service.MarketAnalyzer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("local")
class Test {

    @Autowired
    lateinit var marketAnalyzer: MarketAnalyzer

    @Test
    fun test() {
        println(marketAnalyzer.avgOfPer)
    }
}