plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}

rootProject.name = "firstApp"
