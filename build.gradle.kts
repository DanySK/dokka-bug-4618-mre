plugins {
    kotlin("jvm") version "2.4.21" apply false
    id("org.jetbrains.dokka") apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.dokka")
    extensions.configure<org.jetbrains.dokka.gradle.DokkaExtension> {
        dokkaPublications.configureEach {
            failOnWarning.set(providers.gradleProperty("failOnWarning").map(String::toBoolean).orElse(true))
        }
    }
}
