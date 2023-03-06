package br.com.idws.idempotence.model

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "idempotence_lock", uniqueConstraints = [
        UniqueConstraint(
            name = "uk_idp_k_coll", columnNames = ["idempotenceKey", "collection"]
        )
    ]
)
data class IdempotenceLock(
    @Id
    val id: UUID = UUID.randomUUID(),
    val idempotenceKey: String,
    val collection: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @OneToOne(mappedBy = "idempotenceLock")
    lateinit var process: IdempotentProcess
}