package br.com.idws.idempotence.model

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
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
    @Enumerated(EnumType.STRING)
    val status: Status = Status.PENDING
) {
    fun isPending() = status == Status.PENDING
    fun isSuccess() = status == Status.SUCCESS
    fun isError() = status == Status.ERROR

    fun success() = this.copy(status = Status.SUCCESS)
    fun error() = this.copy(status = Status.ERROR)
}

enum class Status {
    PENDING, SUCCESS, ERROR
}
