package container

import org.testcontainers.containers.wait.strategy.Wait

object PostgresContainer: ContainerWrapper() {

    override val container: Container by lazy {
        Container("postgres:13")
            .withEnv("POSTGRES_DB", "idempotency_test")
            .withEnv("POSTGRES_USER", "postgres")
            .withEnv("POSTGRES_PASSWORD", "postgres")
            .withExposedPorts(originalPort.portNumber)
            .waitingFor(Wait.forLogMessage(".*ready to accept connections.*\\n", 1))
    }

    override val originalPort = OriginalPortProperty(
        propertyName = "POSTGRESQL_DYNAMIC_PORT",
        portNumber = 5432
    )
}
