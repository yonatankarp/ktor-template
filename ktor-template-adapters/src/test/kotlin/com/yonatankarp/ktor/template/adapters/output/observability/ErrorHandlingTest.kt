package com.yonatankarp.ktor.template.adapters.output.observability

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication

class ErrorHandlingTest :
    FunSpec({

        test("returns sanitised JSON 500 on unhandled exception") {
            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    configureErrorHandling()
                    routing {
                        get("/boom") { error("kaboom") }
                    }
                }

                // When
                val response = client.get("/boom")

                // Then
                response.status shouldBe HttpStatusCode.InternalServerError
                response.bodyAsText() shouldBe """{"error":"Internal Server Error"}"""
            }
        }
    })
