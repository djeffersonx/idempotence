package container

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.stream.Collectors
import javax.sql.DataSource

class DatabaseExtension : BeforeAllCallback, AfterAllCallback, AfterEachCallback {

    private lateinit var applicationContext: ApplicationContext
    private lateinit var dataSource: DataSource
    private lateinit var dataSourceUsername: String

    override fun beforeAll(context: ExtensionContext) {
        PostgresContainer.container.start()

        applicationContext = SpringExtension.getApplicationContext(context)
        val environment = applicationContext.environment

        dataSourceUsername = environment.getRequiredProperty("spring.datasource.username")
        dataSource = applicationContext.getBean(DataSource::class.java)

    }

    override fun afterAll(context: ExtensionContext) {
        PostgresContainer.container.stop()
    }

    override fun afterEach(context: ExtensionContext) {
        truncateTables()
    }

    private fun truncateTables() {

        val ignoredTables = listOf("flyway_schema_history")
            .joinToString("','", "'", "'")

        val truncateTablesSql = """
                CREATE OR REPLACE FUNCTION truncate_tables(username IN VARCHAR) RETURNS void AS $$
                DECLARE
                    statements CURSOR FOR
                        SELECT tablename FROM pg_tables
                        WHERE tableowner = username AND schemaname = 'public' and tablename not in ($ignoredTables);
                BEGIN
                    FOR stmt IN statements LOOP
                        EXECUTE 'TRUNCATE TABLE ' || quote_ident(stmt.tablename) || ' CASCADE;';
                    END LOOP;
                END;
                $$
                 LANGUAGE plpgsql;
                SELECT truncate_tables('$dataSourceUsername');
            """.trimIndent()

        dataSource.connection.use { con ->
            con.prepareCall(truncateTablesSql).execute()
        }
    }
}
