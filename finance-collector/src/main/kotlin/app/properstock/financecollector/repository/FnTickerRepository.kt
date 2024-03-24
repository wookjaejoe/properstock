package app.properstock.financecollector.repository

import app.properstock.financecollector.model.fn.FnTicker
import org.springframework.data.mongodb.repository.MongoRepository


interface FnTickerRepository : MongoRepository<FnTicker, String>