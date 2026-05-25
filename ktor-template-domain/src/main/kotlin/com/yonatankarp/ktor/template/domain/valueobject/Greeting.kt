package com.yonatankarp.ktor.template.domain.valueobject

import java.util.IllformedLocaleException
import java.util.Locale

@ConsistentCopyVisibility
data class Greeting private constructor(
    val language: Locale,
    val message: String,
) {
    init {
        require(message.isNotBlank()) { "message must not be blank" }
    }

    companion object {
        operator fun invoke(
            language: String,
            message: String,
        ): Greeting = Greeting(parseLanguageTag(language), message)

        private fun parseLanguageTag(tag: String): Locale {
            require(tag.isNotBlank()) {
                "language must be a valid BCP-47 tag (e.g. 'en', 'en-US', 'zh-Hant'), got: '$tag'"
            }
            return try {
                Locale.Builder().setLanguageTag(tag).build()
            } catch (_: IllformedLocaleException) {
                throw IllegalArgumentException(
                    "language must be a valid BCP-47 tag (e.g. 'en', 'en-US', 'zh-Hant'), got: '$tag'",
                )
            }
        }
    }
}
