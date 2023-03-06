package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.Idempotent
import br.com.idws.idempotence.model.IdempotenceLock
import br.com.idws.idempotence.model.Status
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class IdempotentProcessExecutor(
    private val updateProcessStatus: UpdateProcessStatus
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    operator fun <R> invoke(
        lock: IdempotenceLock,
        process: Idempotent<R>
    ): R = try {

        logger.info("[${process.key}] - Running main function")

        process.execute()
            .also {
                updateProcessStatus(
                    lock,
                    Status.SUCCESS
                )
            }

    } catch (ex: Throwable) {

        logger.error("[${process.key}] - Error on main function. (acceptRetry: ${process.acceptRetry})", ex)

        updateProcessStatus(lock, Status.ERROR)
        process.onError?.invoke()

        throw ex
    }

}
