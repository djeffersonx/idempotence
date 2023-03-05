package br.com.idws.idempotence.repository

import br.com.idws.idempotence.model.IdempotenceLock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IdempotenceLockRepository : JpaRepository<IdempotenceLock, UUID> {

    @Query(
        value = "SELECT * FROM idempotence_lock i WHERE i.idempotence_key = ?1 FOR UPDATE",
        nativeQuery = true
    )
    fun findForUpdate(idempotenceKey: String, collection: String): IdempotenceLock?


    @Query(
        value = "SELECT * FROM idempotence_lock i WHERE i.idempotence_key = ?1 FOR UPDATE SKIP LOCKED",
        nativeQuery = true
    )
    fun findForUpdateSkipLocked(idempotenceKey: String, collection: String): IdempotenceLock?

}