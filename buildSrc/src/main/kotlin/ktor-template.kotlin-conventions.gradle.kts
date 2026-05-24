import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("jvm")
    id("com.diffplug.spotless")
}

repositories { mavenCentral() }

val libs = the<LibrariesForLibs>()

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
    }
    compilerOptions {
        // TODO(kotlin 2.4): explicit backing fields are expected to stabilize in 2.4 — drop this flag once we upgrade.
        freeCompilerArgs.add("-Xexplicit-backing-fields")
        // TODO(kotlin 2.4): kotlin.uuid.Uuid is expected to stabilize in 2.4 — drop this opt-in once we upgrade.
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
    }
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint("1.5.0")
    }
    kotlinGradle {
        target("*.gradle.kts", "buildSrc/**/*.gradle.kts")
        ktlint("1.5.0")
    }
}
