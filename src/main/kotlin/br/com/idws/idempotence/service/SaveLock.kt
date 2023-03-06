package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.Idempotent
import br.com.idws.idempotence.model.IdempotenceLock
import br.com.idws.idempotence.model.IdempotentProcess
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import br.com.idws.idempotence.repository.IdempotentProcessRepository
import br.com.idws.idempotence.service.exception.LockUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class SaveLock(
    private val idempotenceLockRepository: IdempotenceLockRepository,
    private val idempotentProcessRepository: IdempotentProcessRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional(
        propagation = Propagation.REQUIRES_NEW
    )
    operator fun <R> invoke(
        process: Idempotent<R>
    ): IdempotenceLock = try {
        val process = saveProcess(saveLock(process))
        process.idempotenceLock.process = process
        process.idempotenceLock
    } catch (ex: DataIntegrityViolationException) {
        logger.error("[${process.key}] - Error loading the lock", ex)
        throw LockUnavailableException(process.key)
    }

    private fun <R> saveLock(process: Idempotent<R>) =
        idempotenceLockRepository.save(
            IdempotenceLock(
                idempotenceKey = process.key,
                collection = process.collection
            )
        )

    private fun saveProcess(lock: IdempotenceLock) =
        idempotentProcessRepository.save(
            IdempotentProcess(
                idempotenceLock = lock
            )
        )
}