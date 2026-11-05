pluginManagement {
    val dokkaVersion = providers.gradleProperty("dokkaVersion").get()
    plugins {
        id("org.jetbrains.dokka") version dokkaVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "dokka-bug-repro"
include("a", "b")
