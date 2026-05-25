package com.yonatankarp.ktor.template.adapters.input.http.rest

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withTimeoutOrNull
import javax.sql.DataSource

fun Route.readyRoutes() {
    val dataSource: DataSource by application.dependencies
    get("/ready") {
        if (dataSource.canServe()) {
            call.respondText("READY")
        } else {
            call.respondText("NOT READY", status = HttpStatusCode.ServiceUnavailable)
        }
    }
}

private suspend fun DataSource.canServe(): Boolean =
    withTimeoutOrNull(READY_CHECK_TIMEOUT_MS) {
        try {
            runInterruptible(Dispatchers.IO) {
                connection.use { it.isValid(READY_CHECK_DRIVER_TIMEOUT_SEC) }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    } ?: false

private const val READY_CHECK_TIMEOUT_MS = 1_000L
private const val READY_CHECK_DRIVER_TIMEOUT_SEC = 1
