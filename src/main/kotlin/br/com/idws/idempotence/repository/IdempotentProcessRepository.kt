package br.com.idws.idempotence.repository

import br.com.idws.idempotence.model.IdempotentProcess
import br.com.idws.idempotence.model.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IdempotentProcessRepository : JpaRepository<IdempotentProcess, UUID> {

    @Modifying
    @Query("update IdempotentProcess set status = :status where id = :id")
    fun updateStatus(id: UUID, status: Status)

}