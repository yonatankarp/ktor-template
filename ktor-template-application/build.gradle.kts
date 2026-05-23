plugins {
    id("ktor-template.kotlin-conventions")
    id("ktor-template.code-metrics")
}

// Application layer: input ports (use case interfaces) and use case implementations.
// Depends only on :ktor-template-domain. No framework code here — use cases are pure
// Kotlin and call out to ports (interfaces); :ktor-template-adapters provides the
// adapters that implement those ports against real infrastructure.

dependencies {
    implementation(project(":ktor-template-domain"))

    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
}
