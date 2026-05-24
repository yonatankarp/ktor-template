package com.yonatankarp.ktor.template.adapters.input.http.rest

import com.yonatankarp.ktor.template.testing.bootWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.testcontainers.containers.PostgreSQLContainer

class GreetingsApiIntegrationTest :
    FunSpec({

        val postgres = PostgreSQLContainer("postgres:18-alpine")

        beforeSpec { postgres.start() }
        afterSpec { postgres.stop() }

        test("GET /greetings/random returns a seeded greeting") {
            testApplication {
                // Given
                bootWith(postgres)

                // When
                val response = client.get("/greetings/random")

                // Then
                response.status shouldBe HttpStatusCode.OK
                val body = Json.decodeFromString<GreetingResponse>(response.bodyAsText())
                SEEDED_LANGUAGES shouldContain body.language
                body.message.shouldNotBeBlank()
            }
        }

        test("GET /health returns OK") {
            testApplication {
                // Given
                bootWith(postgres)

                // When
                val response = client.get("/health")

                // Then
                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe "OK"
            }
        }
    }) {
    companion object {
        private val SEEDED_LANGUAGES = setOf("en", "es", "fr", "de", "ja")
    }
}
