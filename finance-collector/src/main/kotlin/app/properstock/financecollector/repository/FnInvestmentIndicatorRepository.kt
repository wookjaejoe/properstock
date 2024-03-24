package app.properstock.financecollector.repository

import app.properstock.financecollector.model.fn.FnInvestmentIndicator
import app.properstock.financecollector.model.fn.FnTicker
import org.springframework.data.mongodb.repository.MongoRepository

interface FnInvestmentIndicatorRepository : MongoRepository<FnInvestmentIndicator, String>