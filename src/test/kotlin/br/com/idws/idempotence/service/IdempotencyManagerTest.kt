package br.com.idws.idempotence.service

class IdempotencyManagerTest {

//    private val repository = mockk<IdempotencyRepository>()
//    private val idempotencyManager = IdempotencyManager(repository)
//
//    @Nested
//    inner class WithoutDurationConfig {
//
//        @Test
//        fun `it call a main lambda when doesn't exists idempotency register`() {
//            val idempotentProcess = IdempotentProcessBuilder()
//                .apply {
//                    duration = null
//                }.build(
//                    { "mainResult" },
//                    { "fallbackResult" }
//                )
//            every { repository.findById(idempotentProcess.key) } returns Optional.ofNullable(null)
//            every { repository.save(any()) } returnsArgument 0
//
//            val result = idempotencyManager.execute(idempotentProcess)
//
//            verify { repository.save(any()) }
//            Assertions.assertTrue { result == "mainResult" }
//        }
//
//        @Test
//        fun `it call a fallback lambda when exists idempotency register`() {
//            val idempotentProcess = IdempotentProcessBuilder()
//                .apply {
//                    duration = null
//                }.build(
//                    { "mainResult" },
//                    { "fallbackResult" }
//                )
//            every { repository.findById(idempotentProcess.key) } returns Optional.of(Idempotency(idempotentProcess.key))
//
//            val result = idempotencyManager.execute(idempotentProcess)
//
//            Assertions.assertTrue { result == "fallbackResult" }
//        }
//
//        @Test
//        fun `it call a fallback lambda when insert throws DuplicatedKeyException`() {
//            val idempotentProcess = IdempotentProcessBuilder()
//                .apply {
//                    duration = null
//                }.build(
//                    { "mainResult" },
//                    { "fallbackResult" }
//                )
//            every { repository.findById(idempotentProcess.key) } returns Optional.ofNullable(null)
//            every { repository.save(any()) } throws DuplicateKeyException("Idempotency key duplicated")
//
//            val result = idempotencyManager.execute(idempotentProcess)
//
//            verify { repository.save(any()) }
//            Assertions.assertTrue { result == "fallbackResult" }
//
//        }
//
//    }


}