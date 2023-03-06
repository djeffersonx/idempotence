package br.com.idws.idempotence.service

import br.com.idws.idempotence.dsl.Idempotent
import br.com.idws.idempotence.model.Status
import br.com.idws.idempotence.repository.IdempotenceLockRepository
import br.com.idws.idempotence.service.exception.LockUnavailableException
import container.DatabaseExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.function.Supplier

@ExtendWith(value = [SpringExtension::class, DatabaseExtension::class])
@SpringBootTest
class IdempotenceLockManagerIntegrationTest {

    @Autowired
    private lateinit var idempotenceManager: IdempotenceManager

    @Autowired
    private lateinit var repository: IdempotenceLockRepository

    @Test
    fun `it call the main function only once when the key is the same`() {
        val idempotencyKey = UUID.randomUUID().toString()

        val process = `given an idempotent process`(idempotencyKey)

        val firstExecutionResult = idempotenceManager.execute(process)
        val secondExecutionResult = idempotenceManager.execute(process)

        Assertions.assertEquals("mainRun", firstExecutionResult)
        Assertions.assertEquals("onAlreadyExecuted", secondExecutionResult)
    }


    @Nested
    inner class Retry {

        @Test
        fun `it doesn't retry when isn't configured to do it`() {
            val idempotencyKey = UUID.randomUUID().toString()

            val processWithError = `given a proces with error`(idempotencyKey, acceptRetry = false)

            Assertions.assertThrows(Exception::class.java) {
                idempotenceManager.execute(processWithError)
            }

            repository.findForUpdateSkipLockedBy(processWithError.key, processWithError.collection).let { savedLock ->
                Assertions.assertNotNull(savedLock)
                Assertions.assertEquals(Status.ERROR, savedLock?.process?.status)
            }

            Assertions.assertThrows(LockUnavailableException::class.java) {
                idempotenceManager.execute(`given an idempotent process`(idempotencyKey, acceptRetry = false))
            }

        }

        @Test
        fun `it retry when is to do it`() {

            val idempotencyKey = UUID.randomUUID().toString()

            val processWithError = `given a proces with error`(idempotencyKey, acceptRetry = true)

            Assertions.assertThrows(Exception::class.java) {
                idempotenceManager.execute(
                    processWithError
                )
            }

            repository.findForUpdateSkipLockedBy(processWithError.key, processWithError.collection).let { savedLock ->
                Assertions.assertNotNull(savedLock)
                Assertions.assertEquals(Status.ERROR, savedLock?.process?.status)
            }

            val successExecution =
                idempotenceManager.execute(`given an idempotent process`(idempotencyKey, acceptRetry = true))

            Assertions.assertEquals("mainRun", successExecution)

            repository.findForUpdateSkipLockedBy(processWithError.key, processWithError.collection).let { savedLock ->
                Assertions.assertNotNull(savedLock)
                Assertions.assertEquals(Status.SUCCESS, savedLock?.process?.status)
            }

        }

    }

    private fun `given an idempotent process`(key: String, acceptRetry: Boolean = false) =
        Idempotent<String>(key, "ROOT") {

            acceptRetry(acceptRetry)

            execute {
                "mainRun"
            }
            onAlreadyExecuted {
                "onAlreadyExecuted"
            }
            onError {
                println("Error processing main function ...")
            }
        }

    private fun `given a proces with error`(idempotencyKey: String, acceptRetry: Boolean = false) =
        Idempotent<String>(idempotencyKey, "ROOT") {

            this.acceptRetry = acceptRetry

            execute {
                throw Exception("Error processing")
            }

            onAlreadyExecuted {
                "onAlreadyExecuted"
            }

            onError {
                println("Error processing main function ...")
            }

        }

    private fun <T : Any> doInMultipleThreads(threadCount: Int, f: () -> T): List<T?> {
        val pool = Executors.newFixedThreadPool(threadCount)

        val semaphore = Semaphore(0)
        val supplier = Supplier {
            semaphore.acquire()
            runCatching(f).getOrNull()
        }

        val tasks = (1..threadCount).map {
            CompletableFuture.supplyAsync(supplier, pool)
        }
        semaphore.release(threadCount)

        return tasks.map { it.join() }
    }

}