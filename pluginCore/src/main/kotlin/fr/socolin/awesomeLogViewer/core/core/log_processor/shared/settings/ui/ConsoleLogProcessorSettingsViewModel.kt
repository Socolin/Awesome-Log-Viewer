package fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui

import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.ConsoleLogProcessorSettingsState

open class ConsoleLogProcessorSettingsViewModel<T : ConsoleLogProcessorSettingsState> :
    LogProcessorSettingsViewModel<T>() {
    var readDebugOutput: Boolean = true
    var readConsoleOutput: Boolean = false

    override fun updateModel(settings: T) {
        super.updateModel(settings)
        readDebugOutput = settings.readDebugOutput.value
        readConsoleOutput = settings.readConsoleOutput.value
    }

    override fun settingsEquals(settings: T): Boolean {
        if (readDebugOutput != settings.readDebugOutput.value) return false
        if (readConsoleOutput != settings.readConsoleOutput.value) return false
        return super.settingsEquals(settings)
    }

    override fun applyChangesTo(settings: T) {
        super.applyChangesTo(settings)
        settings.readDebugOutput.value = readDebugOutput
        settings.readConsoleOutput.value = readConsoleOutput
    }
}

