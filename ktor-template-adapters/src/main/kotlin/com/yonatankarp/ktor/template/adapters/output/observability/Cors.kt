package com.yonatankarp.ktor.template.adapters.output.observability

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    install(CORS) {
        anyMethod()
        allowHeader(HttpHeaders.ContentType)
    }
}
