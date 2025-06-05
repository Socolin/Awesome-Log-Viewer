package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.ui

import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.ConsoleLogProcessorSettingsViewModel
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.NetworkLogProcessorSettingsViewModel
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.ApplicationInsightsDefaultColors
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsConsoleSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsNetworkSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.GlobalApplicationInsightsSettingsState
import java.awt.Color

class ApplicationInsightsProcessorSettingsViewModel {
    val console = ApplicationInsightsConsoleProcessorSettingsViewModel()
    val network = ApplicationInsightsNetworkProcessorSettingsViewModel()

    var metricColor: Color = ApplicationInsightsDefaultColors.Companion.metricColor
    var exceptionColor: Color = ApplicationInsightsDefaultColors.Companion.exceptionColor
    var messageColor: Color = ApplicationInsightsDefaultColors.Companion.messageColor
    var dependencyColor: Color = ApplicationInsightsDefaultColors.Companion.dependencyColor
    var requestColor: Color = ApplicationInsightsDefaultColors.Companion.requestColor
    var eventColor: Color = ApplicationInsightsDefaultColors.Companion.eventColor
    var pageViewColor: Color = ApplicationInsightsDefaultColors.Companion.pageViewColor

    fun updateModel(state: ApplicationInsightsSettingsState) {
        console.updateModel(state.consoleSettings)
        network.updateModel(state.networkSettings)
    }

    fun updateModel(globalState: GlobalApplicationInsightsSettingsState) {
        metricColor = globalState.metricColor.value
        exceptionColor = globalState.exceptionColor.value
        messageColor = globalState.messageColor.value
        dependencyColor = globalState.dependencyColor.value
        requestColor = globalState.requestColor.value
        eventColor = globalState.eventColor.value
        pageViewColor = globalState.pageViewColor.value
    }

    fun settingsEquals(state: ApplicationInsightsSettingsState): Boolean {
        if (!console.settingsEquals(state.consoleSettings)) return false
        if (!network.settingsEquals(state.networkSettings)) return false
        return true
    }

    fun settingsEquals(globalState: GlobalApplicationInsightsSettingsState): Boolean {
        if (!metricColor.equals(globalState.metricColor.value)) return false
        if (!exceptionColor.equals(globalState.exceptionColor.value)) return false
        if (!messageColor.equals(globalState.messageColor.value)) return false
        if (!dependencyColor.equals(globalState.dependencyColor.value)) return false
        if (!requestColor.equals(globalState.requestColor.value)) return false
        if (!eventColor.equals(globalState.eventColor.value)) return false
        if (!pageViewColor.equals(globalState.pageViewColor.value)) return false
        return true
    }

    fun applyChangesTo(applicationInsightsSettings: ApplicationInsightsSettingsState) {
        console.applyChangesTo(applicationInsightsSettings.consoleSettings)
        network.applyChangesTo(applicationInsightsSettings.networkSettings)
    }

    fun applyChangesTo(globalState: GlobalApplicationInsightsSettingsState) {
        globalState.metricColor.set(metricColor)
        globalState.exceptionColor.set(exceptionColor)
        globalState.messageColor.set(messageColor)
        globalState.dependencyColor.set(dependencyColor)
        globalState.requestColor.set(requestColor)
        globalState.eventColor.set(eventColor)
        globalState.pageViewColor.set(pageViewColor)
    }
}

class ApplicationInsightsConsoleProcessorSettingsViewModel
    : ConsoleLogProcessorSettingsViewModel<ApplicationInsightsConsoleSettingsState>() {

    override fun updateModel(settings: ApplicationInsightsConsoleSettingsState) {
        super.updateModel(settings)
    }

    override fun settingsEquals(settings: ApplicationInsightsConsoleSettingsState): Boolean {
        return super.settingsEquals(settings)
    }

    override fun applyChangesTo(settings: ApplicationInsightsConsoleSettingsState) {
        super.applyChangesTo(settings)
    }
}

class ApplicationInsightsNetworkProcessorSettingsViewModel :
    NetworkLogProcessorSettingsViewModel<ApplicationInsightsNetworkSettingsState>() {

    var environmentVariables = mutableMapOf<String, String>()

    override fun updateModel(settings: ApplicationInsightsNetworkSettingsState) {
        environmentVariables = settings.environmentVariables.toMutableMap()
        super.updateModel(settings)
    }

    override fun settingsEquals(settings: ApplicationInsightsNetworkSettingsState): Boolean {
        if (!environmentVariables.contentEquals(settings.environmentVariables)) return false
        return super.settingsEquals(settings)
    }

    override fun applyChangesTo(settings: ApplicationInsightsNetworkSettingsState) {
        settings.environmentVariables.clear()
        settings.environmentVariables.putAll(environmentVariables)
        super.applyChangesTo(settings)
    }
}

fun Map<String, String>.contentEquals(other: Map<String, String>): Boolean {
    if (size != other.size) return false
    return all { (key, value) -> other[key] == value }
}

