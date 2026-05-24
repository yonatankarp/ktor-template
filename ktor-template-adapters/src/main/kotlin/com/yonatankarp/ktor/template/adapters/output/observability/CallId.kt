package com.yonatankarp.ktor.template.adapters.output.observability

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import kotlin.uuid.Uuid

fun Application.configureCallId() {
    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        generate { Uuid.random().toString() }
        verify { it.isNotEmpty() }
        header(HttpHeaders.XRequestId)
    }
}
