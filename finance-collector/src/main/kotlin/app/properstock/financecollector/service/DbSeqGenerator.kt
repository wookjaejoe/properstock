package app.properstock.financecollector.service

import app.properstock.financecollector.model.DbSequence
import app.properstock.financecollector.repository.DbSeqRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class DbSeqGenerator(
    val repository: DbSeqRepository
) {
    fun generate(seqName: String): Mono<DbSequence> =
        repository.findById(seqName)
            .switchIfEmpty {
                Mono.just(DbSequence(seqName, 0))
            }
            .map {
                it.value++
                it
            }
            .flatMap { repository.save(it) }
}