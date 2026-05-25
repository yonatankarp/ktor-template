package com.yonatankarp.ktor.template.adapters.output.observability

import com.yonatankarp.ktor.template.adapters.input.http.rest.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception on ${call.request.local.uri}", cause)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal Server Error"))
        }
    }
}
