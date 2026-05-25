package com.yonatankarp.ktor.template.adapters.input.http.rest

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.testcontainers.postgresql.PostgreSQLContainer
import javax.sql.DataSource

class ReadinessHttpAdapterTest :
    FunSpec({

        val postgres = PostgreSQLContainer("postgres:18-alpine")

        beforeSpec { postgres.start() }
        afterSpec { postgres.stop() }

        test("GET /ready returns 200 READY when the database is reachable") {
            // Given
            val dataSource =
                HikariDataSource(
                    HikariConfig().apply {
                        jdbcUrl = postgres.jdbcUrl
                        username = postgres.username
                        password = postgres.password
                    },
                )
            try {
                testApplication {
                    application {
                        dependencies {
                            provide<DataSource> { dataSource }
                        }
                        routing { readyRoutes() }
                    }

                    // When
                    val response = client.get("/ready")

                    // Then
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe "READY"
                }
            } finally {
                dataSource.close()
            }
        }

        test("GET /ready returns 503 NOT READY when the database is unreachable") {
            // Given
            val brokenDataSource =
                HikariDataSource(
                    HikariConfig().apply {
                        jdbcUrl = "jdbc:postgresql://localhost:1/nope"
                        username = "x"
                        password = "x"
                        connectionTimeout = BROKEN_CONNECT_TIMEOUT_MS
                        initializationFailTimeout = SKIP_INITIAL_VALIDATION
                    },
                )
            try {
                testApplication {
                    application {
                        dependencies {
                            provide<DataSource> { brokenDataSource }
                        }
                        routing { readyRoutes() }
                    }

                    // When
                    val response = client.get("/ready")

                    // Then
                    response.status shouldBe HttpStatusCode.ServiceUnavailable
                    response.bodyAsText() shouldBe "NOT READY"
                }
            } finally {
                brokenDataSource.close()
            }
        }
    }) {
    companion object {
        private const val BROKEN_CONNECT_TIMEOUT_MS = 250L
        private const val SKIP_INITIAL_VALIDATION = -1L
    }
}
