package com.yonatankarp.ktor.template

import com.yonatankarp.ktor.template.adapters.output.persistence.configureDatabase
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureDatabase()
    routing {
        get("/health") {
            call.respondText("OK")
        }
    }
}
