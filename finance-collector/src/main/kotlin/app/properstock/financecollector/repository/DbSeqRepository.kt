package app.properstock.financecollector.repository

import app.properstock.financecollector.model.DbSequence
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface DbSeqRepository : ReactiveMongoRepository<DbSequence, String>