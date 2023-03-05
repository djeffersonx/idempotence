package br.com.idws.idempotence.dsl

class IdempotentProcess<R>(
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

    fun retryEnabled(acceptRetryCalls: Boolean) {
        this.acceptRetry = acceptRetryCalls
    }

}

fun <R> idempotentProcess(
    key: String,
    collection: String,
    initializer: IdempotentProcess<R>.() -> Unit
) = IdempotentProcess<R>(
    key, collection
).apply(initializer)