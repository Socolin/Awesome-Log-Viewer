package fr.socolin.awesomeLogViewer.core.core.settings.ui

import com.intellij.ui.JBColor
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsState
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import java.awt.Color
import kotlin.collections.iterator

class GlobalPluginSettingsViewModel {
    var maxLogCount: Int = 0
    var showTimeFromStart: Boolean = true
    var colorLogBasedOnSeverity: Boolean = true
    var colorPerSeverity: MutableMap<GenericSeverityLevel, Color?> = mutableMapOf()
    var createPendingParent: Boolean = false

    fun updateModel(settings: GlobalPluginSettingsState) {
        maxLogCount = settings.maxLogCount.value
        showTimeFromStart = settings.showTimeFromStart.value
        colorLogBasedOnSeverity = settings.colorLogBasedOnSeverity.value
        colorPerSeverity = settings.colorPerSeverity.map { it.key to it.value }.toMap().toMutableMap()
        createPendingParent = settings.createPendingParent.value
    }

    fun applyChangesTo(settings: GlobalPluginSettingsState) {
        settings.maxLogCount.set(maxLogCount)
        settings.showTimeFromStart.set(showTimeFromStart)
        settings.colorLogBasedOnSeverity.set(colorLogBasedOnSeverity)
        settings.colorPerSeverity.clear()
        for ((key, value) in colorPerSeverity) {
            settings.colorPerSeverity[key] = if (value != null) JBColor(value, value) else null
        }
        settings.createPendingParent.set(createPendingParent)
    }

}

