package app.properstock.financecollector.service

import app.properstock.financecollector.model.DatabaseSequence
import app.properstock.financecollector.repository.DatabaseSequenceRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class DatabaseSequenceGenerator(
    val databaseSequenceRepository: DatabaseSequenceRepository
) {
    fun increaseSequence(seqName: String): Mono<DatabaseSequence> =
        databaseSequenceRepository.findById(seqName)
            .switchIfEmpty {
                databaseSequenceRepository.save(DatabaseSequence(seqName, 0))
            }
            .flatMap {
                it.value++
                databaseSequenceRepository.save(it)
            }
}