package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.IdempotentProcess
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
        process: IdempotentProcess<R>
    ): IdempotenceLock = try {
        findLockWithError(process)
            ?: saveLock(process)
                .let { repository.findForUpdate(process.key, process.collection)!! }
    } catch (ex: DataIntegrityViolationException) {
        logger.error("[${process.key}] Error loading the lock", ex)
        throw LockUnavailableException(process.key)
    }

    private fun <R> findLockWithError(process: IdempotentProcess<R>): IdempotenceLock? {
        return repository.findForUpdate(process.key, process.collection)?.let { lock ->
            if (lock.isError() && process.acceptRetry) {
                lock
            } else {
                throw LockUnavailableException(process.key)
            }
        }
    }
}