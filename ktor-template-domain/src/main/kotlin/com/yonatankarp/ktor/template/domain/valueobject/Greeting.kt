package com.yonatankarp.ktor.template.domain.valueobject

import java.util.IllformedLocaleException
import java.util.Locale

data class Greeting(
    val language: String,
    val message: String,
) {
    init {
        require(isValidBcp47(language)) {
            "language must be a valid BCP-47 tag (e.g. 'en', 'en-US', 'zh-Hant'), got: '$language'"
        }
        require(message.isNotBlank()) { "message must not be blank" }
    }

    companion object {
        private fun isValidBcp47(tag: String): Boolean {
            if (tag.isBlank()) return false
            return try {
                Locale.Builder().setLanguageTag(tag).build()
                true
            } catch (_: IllformedLocaleException) {
                false
            }
        }
    }
}
