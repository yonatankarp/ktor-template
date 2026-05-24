package com.yonatankarp.ktor.template.application.ports.input

import com.yonatankarp.ktor.template.domain.valueobject.Greeting

interface Greet {
    suspend operator fun invoke(): Greeting
}
