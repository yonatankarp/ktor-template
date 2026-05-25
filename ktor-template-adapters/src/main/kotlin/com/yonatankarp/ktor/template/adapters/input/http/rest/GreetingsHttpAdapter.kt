package com.yonatankarp.ktor.template.adapters.input.http.rest

import com.yonatankarp.ktor.template.application.ports.input.Greet
import com.yonatankarp.ktor.template.domain.valueobject.Greeting
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get

fun Route.greetingsRoutes() {
    val greet: Greet by application.dependencies
    get("/greetings/random") {
        call.respond(greet().toResponse())
    }
}

private fun Greeting.toResponse() = GreetingResponse(language = language.toLanguageTag(), message = message)
