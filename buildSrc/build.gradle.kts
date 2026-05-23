plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${libs.versions.spotless.get()}")
    // Expose the generated `libs` version-catalog accessor (LibrariesForLibs) to precompiled convention scripts.
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
