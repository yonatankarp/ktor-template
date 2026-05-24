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

fun Application.configureDatabase() {
    val cfg = environment.config.config("database")
    val dataSource = createDataSource(cfg)

    runCatching {
        runMigrations(dataSource)
        Database.connect(dataSource)

        dependencies {
            provide<DataSource> { dataSource }
        }

        monitor.subscribe(ApplicationStopping) {
            log.info("Closing database connection pool")
            dataSource.close()
        }
    }.onFailure { dataSource.close() }
        .getOrThrow()
}

private fun createDataSource(config: ApplicationConfig): HikariDataSource {
    val hikariConfig =
        HikariConfig().apply {
            jdbcUrl = config.property("url").getString()
            username = config.property("username").getString()
            password = config.property("password").getString()
            maximumPoolSize = config.property("maximumPoolSize").getString().toInt()
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
