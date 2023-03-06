package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.Idempotent
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import br.com.idws.idempotence.service.exception.LockUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IdempotenceManager(
    private val getOrCreateLock: GetOrCreateLock,
    private val run: IdempotentProcessExecutor,
    private val repository: IdempotenceLockRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun <R> execute(
        process: Idempotent<R>
    ) = try {

        run(getOrCreateLock(process), process)

    } catch (ex: LockUnavailableException) {

        logger.error("[${process.key}] - Lock unavailable")

        if (findLock(process)?.process?.isSuccess() == true) {
            process.onAlreadyExecuted()
        } else {
            throw ex // Still PROCESSING or is in ERROR
        }
    }

    private fun <R> findLock(process: Idempotent<R>) =
        repository.findForUpdateSkipLocked(process.key, process.collection)

}

