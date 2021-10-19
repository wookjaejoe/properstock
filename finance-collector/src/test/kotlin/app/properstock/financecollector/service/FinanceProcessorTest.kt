package app.properstock.financecollector.service

import app.properstock.financecollector.repository.TickerRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class FinanceProcessorTest{

    @Autowired
    lateinit var financeProcessor: FinanceProcessor

    @Autowired
    lateinit var tickerRepository: TickerRepository

    @Test
    fun processFinanceAnalysis() {
        listOf(
            "005930",
            "000270",
        ).forEach {
            financeProcessor.processFinanceAnalysis(it)
        }
    }
}