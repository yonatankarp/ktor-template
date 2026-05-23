package com.yonatankarp.ktor.template.adapters.output.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.log
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.di.dependencies
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import javax.sql.DataSource

/**
 * Wires the persistence stack: Hikari connection pool, Flyway migrations,
 * and Exposed's default Database. Registers the [DataSource] with Ktor DI
 * and closes the pool on application shutdown.
 *
 * Configuration is read from the `database` section of `application.yaml`.
 */
fun Application.configureDatabase() {
    val cfg = environment.config.config("database")
    val dataSource = createDataSource(cfg)

    runMigrations(dataSource)
    Database.connect(dataSource)

    dependencies {
        provide<DataSource> { dataSource }
    }

    monitor.subscribe(ApplicationStopping) {
        log.info("Closing database connection pool")
        dataSource.close()
    }
}

private fun createDataSource(config: ApplicationConfig): HikariDataSource {
    val hikariConfig =
        HikariConfig().apply {
            jdbcUrl = config.property("url").getString()
            username = config.property("username").getString()
            password = config.property("password").getString()
            maximumPoolSize =
                config.propertyOrNull("maximumPoolSize")?.getString()?.toInt() ?: 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            poolName = "ktor-template-pool"
            validate()
        }
    return HikariDataSource(hikariConfig)
}

private fun runMigrations(dataSource: DataSource) {
    Flyway
        .configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .load()
        .migrate()
}
