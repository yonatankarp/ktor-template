import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    jacoco
}

val libs = the<LibrariesForLibs>()

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.named("test") {
    finalizedBy("jacocoTestReport")
}
