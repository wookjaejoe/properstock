package app.properstock.financecollector.repository

import app.properstock.financecollector.model.ProperPrice
import org.springframework.data.mongodb.repository.MongoRepository

interface ProperPriceRepository : MongoRepository<ProperPrice, Long> {
    fun findByTickerCodeAndFormulaSymbol(tickerCode: String, formulaSymbol: String): ProperPrice?
}