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
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("spotlessApply")
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
