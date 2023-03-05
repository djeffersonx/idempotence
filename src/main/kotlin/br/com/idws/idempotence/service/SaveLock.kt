package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.IdempotentProcess
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
    private val repository: IdempotenceLockRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional(
        propagation = Propagation.REQUIRES_NEW
    )
    operator fun <R> invoke(
        process: IdempotentProcess<R>
    ): IdempotenceLock = try {
        repository.save(
            IdempotenceLock(
                idempotenceKey = process.key,
                collection = process.collection
            )
        )
    } catch (ex: DataIntegrityViolationException) {
        logger.error("[${process.key}] - Error loading the lock", ex)
        throw LockUnavailableException(process.key)
    }
}