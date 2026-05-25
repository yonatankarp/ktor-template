package com.yonatankarp.ktor.template

import com.yonatankarp.ktor.template.adapters.input.http.rest.greetingsRoutes
import com.yonatankarp.ktor.template.adapters.input.http.rest.healthRoutes
import com.yonatankarp.ktor.template.adapters.input.http.rest.metricsRoutes
import com.yonatankarp.ktor.template.adapters.output.observability.InMemoryEventBus
import com.yonatankarp.ktor.template.adapters.output.observability.configureCallId
import com.yonatankarp.ktor.template.adapters.output.observability.configureCallLogging
import com.yonatankarp.ktor.template.adapters.output.observability.configureCors
import com.yonatankarp.ktor.template.adapters.output.observability.configureDefaultHeaders
import com.yonatankarp.ktor.template.adapters.output.observability.configureErrorHandling
import com.yonatankarp.ktor.template.adapters.output.observability.configureMetrics
import com.yonatankarp.ktor.template.adapters.output.observability.logGreetingDeliveries
import com.yonatankarp.ktor.template.adapters.output.persistence.GreetingExposedCatalog
import com.yonatankarp.ktor.template.adapters.output.persistence.configureDatabase
import com.yonatankarp.ktor.template.application.ports.input.Greet
import com.yonatankarp.ktor.template.application.usecases.GreetUseCase
import com.yonatankarp.ktor.template.domain.event.GreetingDelivered
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.MeterRegistry
import org.jetbrains.exposed.v1.jdbc.Database

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val database = configureDatabase()
    install(ContentNegotiation) { json() }
    configureDefaultHeaders()
    configureCors()
    configureErrorHandling()
    configureCallId()
    configureCallLogging()
    val metricsRegistry = configureMetrics()

    val greet = wireGreetings(database, metricsRegistry)

    routing {
        healthRoutes()
        metricsRoutes(metricsRegistry)
        greetingsRoutes(greet)
    }
}

private fun Application.wireGreetings(
    database: Database,
    metricsRegistry: MeterRegistry,
): Greet {
    val bus = InMemoryEventBus<GreetingDelivered>(metricsRegistry)
    logGreetingDeliveries(bus)
    return GreetUseCase(GreetingExposedCatalog(database), bus)
}
