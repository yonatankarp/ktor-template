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
        verify { it.isNotEmpty() && it.length <= CALL_ID_MAX_LENGTH }
        header(HttpHeaders.XRequestId)
    }
}

private const val CALL_ID_MAX_LENGTH = 64
