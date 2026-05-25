package com.yonatankarp.ktor.template.adapters.output

import io.ktor.server.config.ApplicationConfig

internal fun ApplicationConfig.intProperty(key: String): Int {
    val raw = property(key).getString()
    return raw.toIntOrNull()
        ?: error("$key must be an integer, got: '$raw'")
}

internal fun ApplicationConfig.intPropertyOrNull(key: String): Int? = propertyOrNull(key)?.getString()?.toIntOrNull()

internal fun ApplicationConfig.intPropertyOrDefault(
    key: String,
    default: Int,
): Int = intPropertyOrNull(key) ?: default
