package br.com.idws.idempotence.model

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "idempotent_process")
data class IdempotentProcess(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Enumerated(EnumType.STRING)
    val status: Status = Status.PENDING,
    @OneToOne
    @JoinColumn(name = "idempotence_lock_id")
    val idempotenceLock: IdempotenceLock
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