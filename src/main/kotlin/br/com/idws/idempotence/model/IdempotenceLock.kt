package br.com.idws.idempotence.model

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
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
    @OneToOne(mappedBy = "idempotenceLock", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    lateinit var process: IdempotentProcess

    companion object {
        fun of(idempotenceKey: String, collection: String) =
            IdempotenceLock(
                idempotenceKey = idempotenceKey,
                collection = collection
            ).apply {
                this.process = IdempotentProcess(idempotenceLock = this)
            }
    }

}