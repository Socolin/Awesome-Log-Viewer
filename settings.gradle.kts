rootProject.name = "Awesome Log Viewer"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("pluginCore")
include("processorApplicationInsights")
include("processorOpenTelemetry")
include("processorSimpleConsole")
include("platformSpecificJava")
include("platformSpecificRider")
