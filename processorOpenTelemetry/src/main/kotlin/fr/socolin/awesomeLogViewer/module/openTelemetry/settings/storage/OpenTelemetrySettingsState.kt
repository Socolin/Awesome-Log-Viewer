package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage

import com.intellij.util.xmlb.annotations.MapAnnotation
import com.intellij.util.xmlb.annotations.Tag
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.ConsoleLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.NetworkLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsState

class OpenTelemetrySettingsState : BaseSettingsState() {
    @Tag("consoleSettings")
    val consoleSettings = OpenTelemetryConsoleSettingsState()
    @Tag("networkSettings")
    val networkSettings = OpenTelemetryNetworkSettingsState()

    override fun registerProperties() {
        registerChildState(consoleSettings)
        registerChildState(networkSettings)
    }
}

@Tag("openTelemetryConsoleSettings")
class OpenTelemetryConsoleSettingsState : ConsoleLogProcessorSettingsState() {
    init {
        enableForRun.set(false)
        enableForDebug.set(false)
        readConsoleOutput.set(false)
    }

    override fun registerProperties() {
        super.registerProperties()
    }
}

@Tag("openTelemetryNetworkSettings")
class OpenTelemetryNetworkSettingsState : NetworkLogProcessorSettingsState() {
    init {
        enableForRun.set(true)
        enableForDebug.set(true)
    }

    @MapAnnotation(surroundWithTag = false, surroundKeyWithTag = false, surroundValueWithTag = false)
    val environmentVariables = mutableMapOf(
        Pair("OTEL_EXPORTER_OTLP_ENDPOINT", "http://localhost:\${SERVER_PORT}/"),
        Pair("OTEL_EXPORTER_OTLP_METRICS_ENDPOINT", "http://localhost:\${SERVER_PORT}/v1/metrics"),
        Pair("OTEL_EXPORTER_OTLP_TRACES_ENDPOINT", "http://localhost:\${SERVER_PORT}/v1/traces"),
        Pair("OTEL_EXPORTER_OTLP_LOGS_ENDPOINT", "http://localhost:\${SERVER_PORT}/v1/logs"),
        Pair("OTEL_EXPORTER_OTLP_PROTOCOL", "http/protobuf"),
        Pair("OTEL_METRIC_EXPORT_INTERVAL", "10000"),
    )

    override fun registerProperties() {
        super.registerProperties()
    }
}
