package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.Idempotent
import br.com.idws.idempotence.model.IdempotenceLock
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import br.com.idws.idempotence.service.exception.LockUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class SaveLock(
    private val idempotenceLockRepository: IdempotenceLockRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional(
        propagation = Propagation.REQUIRES_NEW
    )
    operator fun <R> invoke(
        process: Idempotent<R>
    ): IdempotenceLock = try {
        idempotenceLockRepository.save(
            IdempotenceLock.of(process.key, process.collection)
        )

    } catch (ex: DataIntegrityViolationException) {
        logger.error("[${process.key}] - Error loading the lock", ex)
        throw LockUnavailableException(process.key)
    }

}