rootProject.name = "Awesome Log Viewer"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

include("pluginCore")
include("processorApplicationInsights")
include("processorOpenTelemetry")
include("processorSimpleConsole")
include("platformSpecificJava")
include("platformSpecificRider")
