package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

interface OpenTelemetrySignal

enum class OpenTelemetrySignalType(val typeName: String) {
    Trace("Trace"),
    Metric("Metric"),
    Log("Log")
}
