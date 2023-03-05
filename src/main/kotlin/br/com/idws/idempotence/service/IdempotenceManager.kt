package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.IdempotentProcess
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import br.com.idws.idempotence.service.exception.LockUnavailableException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class IdempotenceManager(
    private val getOrCreateLock: GetOrCreateLock,
    private val run: IdempotentProcessExecutor,
    private val repository: IdempotenceLockRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun <R> execute(
        process: IdempotentProcess<R>
    ) = try {

        run(getOrCreateLock(process), process)

    } catch (ex: LockUnavailableException) {

        logger.error("[${process.key}] - Lock unavailable")

        if (repository.findForUpdateSkipLocked(process.key, process.collection)?.isSuccess() == true) {
            process.onAlreadyExecuted()
        } else {
            throw ex // Still PROCESSING or is in ERROR
        }
    }
}

