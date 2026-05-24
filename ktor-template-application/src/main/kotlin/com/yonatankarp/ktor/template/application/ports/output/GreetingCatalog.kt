package com.yonatankarp.ktor.template.application.ports.output

import com.yonatankarp.ktor.template.domain.valueobject.Greeting

interface GreetingCatalog {
    suspend fun random(): Greeting?
}
