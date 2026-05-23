plugins {
    id("ktor-template.kotlin-conventions")
    id("ktor-template.code-metrics")
}

// Pure domain layer. No framework dependencies. No I/O. Only Kotlin stdlib.
// This module is depended on by :ktor-template-application (and transitively
// by :ktor-template-adapters). It MUST NOT depend on anything else in the
// project — that's the whole point of hexagonal architecture and the reason
// this is a separate Gradle module.

dependencies {
    testImplementation(libs.kotlin.test)
}
