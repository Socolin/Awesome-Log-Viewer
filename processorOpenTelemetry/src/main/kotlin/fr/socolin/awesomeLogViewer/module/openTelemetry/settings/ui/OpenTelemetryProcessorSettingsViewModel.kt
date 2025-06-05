package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.ui

import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.ConsoleLogProcessorSettingsViewModel
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.NetworkLogProcessorSettingsViewModel
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.OpenTelemetryDefaultColors
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.GlobalOpenTelemetrySettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetryConsoleSettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetryNetworkSettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetrySettingsState
import java.awt.Color

class OpenTelemetryProcessorSettingsViewModel {
    val console = OpenTelemetryConsoleProcessorSettingsViewModel()
    val network = OpenTelemetryNetworkProcessorSettingsViewModel()

    var metricColor: Color = OpenTelemetryDefaultColors.Companion.metricColor
    var traceColor: Color = OpenTelemetryDefaultColors.Companion.traceColor
    var logRecordColor: Color = OpenTelemetryDefaultColors.Companion.logRecordColor

    fun updateModel(state: OpenTelemetrySettingsState) {
        console.updateModel(state.consoleSettings)
        network.updateModel(state.networkSettings)
    }

    fun updateModel(globalState: GlobalOpenTelemetrySettingsState) {
        metricColor = globalState.metricColor.value
        traceColor = globalState.traceColor.value
        logRecordColor = globalState.logRecordColor.value
    }

    fun settingsEquals(sate: OpenTelemetrySettingsState): Boolean {
        if (!console.settingsEquals(sate.consoleSettings)) return false
        if (!network.settingsEquals(sate.networkSettings)) return false
        return true
    }

    fun settingsEquals(globalState: GlobalOpenTelemetrySettingsState): Boolean {
        if (!metricColor.equals(globalState.metricColor.value)) return false
        if (!traceColor.equals(globalState.traceColor.value)) return false
        if (!logRecordColor.equals(globalState.logRecordColor.value)) return false
        return true
    }

    fun applyChangesTo(state: OpenTelemetrySettingsState) {
        console.applyChangesTo(state.consoleSettings)
        network.applyChangesTo(state.networkSettings)
    }

    fun applyChangesTo(globalState: GlobalOpenTelemetrySettingsState) {
        globalState.metricColor.set(metricColor)
        globalState.traceColor.set(traceColor)
        globalState.logRecordColor.set(logRecordColor)
    }
}

class OpenTelemetryConsoleProcessorSettingsViewModel
    : ConsoleLogProcessorSettingsViewModel<OpenTelemetryConsoleSettingsState>() {

    override fun updateModel(settings: OpenTelemetryConsoleSettingsState) {
        super.updateModel(settings)
    }

    override fun settingsEquals(settings: OpenTelemetryConsoleSettingsState): Boolean {
        return super.settingsEquals(settings)
    }

    override fun applyChangesTo(settings: OpenTelemetryConsoleSettingsState) {
        super.applyChangesTo(settings)
    }
}

class OpenTelemetryNetworkProcessorSettingsViewModel :
    NetworkLogProcessorSettingsViewModel<OpenTelemetryNetworkSettingsState>() {

    var environmentVariables = mutableMapOf<String, String>()

    override fun updateModel(settings: OpenTelemetryNetworkSettingsState) {
        environmentVariables = settings.environmentVariables.toMutableMap()
        super.updateModel(settings)
    }

    override fun settingsEquals(settings: OpenTelemetryNetworkSettingsState): Boolean {
        if (!environmentVariables.contentEquals(settings.environmentVariables)) return false
        return super.settingsEquals(settings)
    }

    override fun applyChangesTo(settings: OpenTelemetryNetworkSettingsState) {
        settings.environmentVariables.clear()
        settings.environmentVariables.putAll(environmentVariables)
        super.applyChangesTo(settings)
    }
}

fun Map<String, String>.contentEquals(other: Map<String, String>): Boolean {
    if (size != other.size) return false
    return all { (key, value) -> other[key] == value }
}
