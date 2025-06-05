package fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui

import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.NetworkLogProcessorSettingsState

open class NetworkLogProcessorSettingsViewModel<T : NetworkLogProcessorSettingsState> :
    LogProcessorSettingsViewModel<T>() {
    var listenToRandomPort: Boolean = true
    var listenPortNumber: Int = 0

    override fun updateModel(settings: T) {
        super.updateModel(settings)
        listenPortNumber = settings.listenPortNumber.value
        listenToRandomPort = settings.listenPortNumber.value == 0
    }

    override fun settingsEquals(settings: T): Boolean {
        if (listenToRandomPort) {
            if (settings.listenPortNumber.value != 0) return false
        } else {
            if (listenPortNumber != settings.listenPortNumber.value) return false
        }
        return super.settingsEquals(settings)
    }

    override fun applyChangesTo(settings: T) {
        super.applyChangesTo(settings)
        settings.listenPortNumber.value = if (listenToRandomPort) 0 else listenPortNumber
    }
}
