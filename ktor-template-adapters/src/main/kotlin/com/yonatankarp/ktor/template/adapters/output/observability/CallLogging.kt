package com.yonatankarp.ktor.template.adapters.output.observability

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.configureCallLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> QUIET_PATHS.none { call.request.path().startsWith(it) } }
        callIdMdc("call-id")
        format { call ->
            "${call.request.httpMethod.value} ${call.request.path()} -> ${call.response.status()}"
        }
    }
}

private val QUIET_PATHS = listOf("/metrics", "/health")
