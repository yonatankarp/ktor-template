package com.yonatankarp.ktor.template.adapters.input.http.rest

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
)
