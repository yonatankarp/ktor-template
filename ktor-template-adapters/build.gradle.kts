plugins {
    id("ktor-template.kotlin-conventions")
    id("ktor-template.code-metrics")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

// Adapters layer: Ktor entry point, input/http adapters, output/persistence adapters,
// and the DI/composition wiring. The only module that depends on Ktor, Exposed,
// JDBC drivers, etc.

application {
    mainClass.set("com.yonatankarp.ktor.template.ApplicationKt")
}

tasks.named<JavaExec>("run") {
    args = listOf("-config=classpath:application-dev.yaml")
}

ktor {
    fatJar {
        archiveFileName.set("ktor-template.jar")
    }
}

dependencies {
    implementation(project(":ktor-template-application"))

    implementation(libs.bundles.ktor.server.core.all)
    implementation(libs.bundles.ktor.server.observability.all)
    implementation(libs.ktor.server.di)
    implementation(libs.bundles.persistence.all)
    implementation(libs.logback.classic)

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.test.unit)
    testImplementation(libs.bundles.test.integration.extras)
}
