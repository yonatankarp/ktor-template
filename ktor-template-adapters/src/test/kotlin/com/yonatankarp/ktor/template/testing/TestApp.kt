package com.yonatankarp.ktor.template.testing

import com.yonatankarp.ktor.template.module
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import org.testcontainers.containers.PostgreSQLContainer

fun ApplicationTestBuilder.bootWith(postgres: PostgreSQLContainer<*>) {
    environment {
        config =
            MapApplicationConfig(
                "ktor.environment" to "test",
                "ops.port" to "0",
                "database.url" to postgres.jdbcUrl,
                "database.username" to postgres.username,
                "database.password" to postgres.password,
                "database.maximumPoolSize" to "5",
            )
    }
    application { module() }
}
