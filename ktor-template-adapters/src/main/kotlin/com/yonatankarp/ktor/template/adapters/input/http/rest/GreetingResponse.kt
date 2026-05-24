package com.yonatankarp.ktor.template.adapters.input.http.rest

import kotlinx.serialization.Serializable

@Serializable
data class GreetingResponse(
    val language: String,
    val message: String,
)
