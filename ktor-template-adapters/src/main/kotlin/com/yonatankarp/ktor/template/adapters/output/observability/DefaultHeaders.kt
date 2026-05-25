package com.yonatankarp.ktor.template.adapters.output.observability

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders

fun Application.configureDefaultHeaders() {
    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
        header("Referrer-Policy", "no-referrer")
    }
}
