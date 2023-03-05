package br.com.idws.idempotence.service

import br.com.idws.idempotence.model.IdempotenceLock
import br.com.idws.idempotence.model.Status
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLockStatus(
    private val repository: IdempotenceLockRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional(
        propagation = Propagation.REQUIRES_NEW
    )
    operator fun invoke(
        idempotenceLock: IdempotenceLock,
        status: Status
    ) = logger.info("[${idempotenceLock.idempotenceKey}] - Updating lock status: $status").let {
        repository.save(idempotenceLock.copy(status = status))
    }

}