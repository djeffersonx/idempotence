package br.com.idws.idempotence.repository

import br.com.idws.idempotence.model.IdempotenceLock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IdempotenceLockRepository : JpaRepository<IdempotenceLock, UUID> {

    @Query(
        value = """SELECT * FROM idempotence_lock il WHERE il.idempotence_key = ?1 and il.collection = ?2""",
        nativeQuery = true
    )
    fun findBy(idempotenceKey: String, collection: String): IdempotenceLock?

    @Query(
        value = """SELECT * FROM idempotence_lock il WHERE il.idempotence_key = ?1 and il.collection = ?2 FOR UPDATE""",
        nativeQuery = true
    )
    fun findForUpdateBy(idempotenceKey: String, collection: String): IdempotenceLock?

    @Query(
        value = """SELECT * FROM idempotence_lock il
            inner join idempotent_process ip on ip.idempotence_lock_id = il.id
            WHERE il.idempotence_key = ?1 and il.collection = ?2 
            FOR UPDATE SKIP LOCKED""",
        nativeQuery = true
    )
    fun findForUpdateSkipLockedBy(idempotenceKey: String, collection: String): IdempotenceLock?

}