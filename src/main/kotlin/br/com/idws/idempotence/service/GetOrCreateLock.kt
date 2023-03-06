package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.Idempotent
import br.com.idws.idempotence.model.IdempotenceLock
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import br.com.idws.idempotence.service.exception.LockUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class GetOrCreateLock(
    private val repository: IdempotenceLockRepository,
    private val saveLock: SaveLock
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    operator fun <R> invoke(
        process: Idempotent<R>
    ): IdempotenceLock = try {
        findLock(process)
            ?: saveLock(process)
                .let { repository.findForUpdateBy(process.key, process.collection)!! }
    } catch (ex: DataIntegrityViolationException) {
        logger.error("[${process.key}] Error loading the lock", ex)
        throw LockUnavailableException(process.key)
    }

    private fun <R> findLock(process: Idempotent<R>): IdempotenceLock? {
        return repository.findForUpdateBy(process.key, process.collection)?.let { lock ->
            if (lock.process.isError() && process.acceptRetry) {
                lock
            } else {
                throw LockUnavailableException(process.key)
            }
        }
    }
}