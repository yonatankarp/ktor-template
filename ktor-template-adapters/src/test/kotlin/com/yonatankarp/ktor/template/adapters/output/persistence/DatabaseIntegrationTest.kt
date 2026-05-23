package com.yonatankarp.ktor.template.adapters.output.persistence

import com.yonatankarp.ktor.template.module
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

class DatabaseIntegrationTest :
    FunSpec({

        val postgres = PostgreSQLContainer("postgres:18-alpine")

        beforeSpec { postgres.start() }
        afterSpec { postgres.stop() }

        test("flyway migrations apply, /health is up, and Exposed can query the schema") {
            testApplication {
                // Given
                environment {
                    config =
                        MapApplicationConfig(
                            "ktor.environment" to "test",
                            "database.url" to postgres.jdbcUrl,
                            "database.username" to postgres.username,
                            "database.password" to postgres.password,
                            "database.maximumPoolSize" to "5",
                        )
                }
                application { module() }

                // When
                val healthResponse = client.get("/health")
                val rowCount =
                    transaction {
                        exec("SELECT COUNT(*) FROM app_health") { rs ->
                            rs.next()
                            rs.getInt(1)
                        }
                    }

                // Then
                healthResponse.status shouldBe HttpStatusCode.OK
                rowCount shouldBe 0
            }
        }
    })
