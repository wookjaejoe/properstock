package app.properstock.financecollector.repository

import app.properstock.financecollector.model.ProperPrice
import org.springframework.data.mongodb.repository.MongoRepository

interface ProperPriceRepository : MongoRepository<ProperPrice, String> {
    fun findByTickerCodeAndFormulaSymbol(tickerCode: String, formulaSymbol: String): ProperPrice?
    fun findAllByTickerCodeIn(tickerCodes: List<String>): List<ProperPrice>
}