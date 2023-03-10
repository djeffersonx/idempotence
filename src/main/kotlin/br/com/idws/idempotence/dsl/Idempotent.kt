package br.com.idws.idempotence.dsl

class Idempotent<R>(
    val key: String,
    val collection: String
) {

    lateinit var execute: () -> R
    lateinit var onAlreadyExecuted: () -> R
    var onError: (() -> Unit)? = null
    var acceptRetry = false

    fun execute(main: () -> R) {
        this.execute = main
    }

    fun onAlreadyExecuted(onAlreadyExecuted: () -> R) {
        this.onAlreadyExecuted = onAlreadyExecuted
    }

    fun onError(onError: () -> Unit) {
        this.onError = onError
    }

    fun acceptRetry(acceptRetryCalls: Boolean) {
        this.acceptRetry = acceptRetryCalls
    }

}

fun <R> Idempotent(
    key: String,
    collection: String,
    initializer: Idempotent<R>.() -> Unit
) = Idempotent<R>(
    key, collection
).apply(initializer)