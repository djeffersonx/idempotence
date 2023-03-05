package builders

import br.com.idws.idempotence.dsl.idempotentProcess
import java.time.Duration
import java.util.UUID

class IdempotentProcessBuilder {

    var key: String = UUID.randomUUID().toString()
    var group: String = "ROOT"
    var duration: Duration? = Duration.ofMinutes(5)

    fun <R> build(
        main: () -> R,
        fallback: () -> R
    ) = idempotentProcess<R>(key, group) {
        execute {
            main()
        }
        onAlreadyExecuted {
            fallback()
        }
    }

}