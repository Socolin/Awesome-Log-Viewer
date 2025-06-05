package fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui

import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.LogProcessorSettingsState

open class LogProcessorSettingsViewModel<T : LogProcessorSettingsState> {
    var enableForRun: Boolean = false
    var enableForDebug: Boolean = true

    open fun updateModel(settings: T) {
        enableForRun = settings.enableForRun.value
        enableForDebug = settings.enableForDebug.value
    }

    open fun settingsEquals(settings: T): Boolean {
        if (enableForRun != settings.enableForRun.value) return false
        if (enableForDebug != settings.enableForDebug.value) return false
        return true
    }

    open fun applyChangesTo(settings: T) {
        settings.enableForRun.value = enableForRun
        settings.enableForDebug.value = enableForDebug
    }
}
