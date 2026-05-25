package com.yonatankarp.ktor.template.adapters.input.http.rest

import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun Route.metricsRoutes() {
    val registry: PrometheusMeterRegistry by application.dependencies
    get("/metrics") {
        call.respondText(registry.scrape())
    }
}
