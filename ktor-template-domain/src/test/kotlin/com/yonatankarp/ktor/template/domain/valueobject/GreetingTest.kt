package com.yonatankarp.ktor.template.domain.valueobject

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GreetingTest :
    FunSpec({

        test("accepts valid BCP-47 language tags") {
            listOf(
                "en",
                "fr",
                "ja",
                "pt-BR",
                "en-US",
                "en-us",
                "zh-Hant",
                "zh-Hant-CN",
                "sr-Latn-RS",
                "es-419",
            ).forEach { language ->
                Greeting(language = language, message = "Hello").language shouldBe language
            }
        }

        test("rejects invalid language tags") {
            listOf(
                "e",
                "en_US",
                "",
                "   ",
            ).forEach { language ->
                shouldThrow<IllegalArgumentException> {
                    Greeting(language = language, message = "Hi")
                }
            }
        }

        test("rejects blank or whitespace-only message") {
            listOf("", " ", "   ", "\t", "\n").forEach { message ->
                shouldThrow<IllegalArgumentException> {
                    Greeting(language = "en", message = message)
                }
            }
        }

        test("two greetings with the same attributes are equal") {
            val a = Greeting(language = "en", message = "Hi")
            val b = Greeting(language = "en", message = "Hi")

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        test("greetings differ when language differs") {
            val a = Greeting(language = "en", message = "Hi")
            val b = Greeting(language = "fr", message = "Hi")

            a shouldNotBe b
        }
    })
