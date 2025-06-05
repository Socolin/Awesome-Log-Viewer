package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.ui

import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.ConsoleLogProcessorSettingsViewModel
import fr.socolin.awesomeLogViewer.module.simpleConsole.LogParsingMethod
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleConsoleSettingsState
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleSettingsState
import java.util.*

class SimpleConsoleProcessorSettingsViewModel {
    val consoles = mutableListOf<SimpleConsoleConsoleProcessorSettingsViewModel>()

    fun updateModel(settings: SimpleConsoleSettingsState) {
        for (consoleSettings in settings.consolesSettings) {
            val consoleViewModel = consoles.firstOrNull { it.runtimeId == consoleSettings.getRuntimeId() }
            if (consoleViewModel == null) {
                consoles.add(SimpleConsoleConsoleProcessorSettingsViewModel(consoleSettings.getRuntimeId()).apply {
                    updateModel(consoleSettings)
                })
            } else {
                consoleViewModel.updateModel(consoleSettings)
            }
        }
        val toRemove = mutableListOf<SimpleConsoleConsoleProcessorSettingsViewModel>()
        for (consoleViewModel in consoles) {
            val consoleSettings =
                settings.consolesSettings.firstOrNull { it.getRuntimeId() == consoleViewModel.runtimeId }
            if (consoleSettings == null) {
                toRemove.add(consoleViewModel)
            }
        }
        toRemove.forEach { consoles.remove(it) }
    }

    fun addConsole(): SimpleConsoleConsoleProcessorSettingsViewModel {
        val consoleViewModel = SimpleConsoleConsoleProcessorSettingsViewModel(UUID.randomUUID()).apply {
            id = "myAwesomeConsoleId${consoles.size + 1}"
        }
        consoles.add(consoleViewModel)
        return consoleViewModel
    }

    fun removeConsole(consoleViewModel: SimpleConsoleConsoleProcessorSettingsViewModel) {
        consoles.remove(consoleViewModel)
    }

    fun settingsEquals(settings: SimpleConsoleSettingsState): Boolean {
        if (settings.consolesSettings.size != consoles.size) return false

        for (consoleSettings in settings.consolesSettings) {
            val consoleViewModel = consoles.firstOrNull { it.runtimeId == consoleSettings.getRuntimeId() }
            if (consoleViewModel == null) {
                return false
            }
            if (!consoleViewModel.settingsEquals(consoleSettings)) {
                return false
            }
        }
        for (consoleViewModel in consoles) {
            val consoleSettings = settings.consolesSettings.firstOrNull { it.getRuntimeId() == consoleViewModel.runtimeId }
            if (consoleSettings == null) {
                return false
            }
            if (!consoleViewModel.settingsEquals(consoleSettings)) {
                return false
            }
        }

        return true
    }

    fun applyChangesTo(settings: SimpleConsoleSettingsState) {
        val toRemove = mutableListOf<SimpleConsoleConsoleSettingsState>()
        for (consoleSettings in settings.consolesSettings) {
            val consoleViewModel = consoles.firstOrNull { it.runtimeId == consoleSettings.getRuntimeId() }
            if (consoleViewModel != null) {
                consoleViewModel.applyChangesTo(consoleSettings)
            } else {
                toRemove.add(consoleSettings)
            }
        }
        toRemove.forEach { settings.removeConsoleSettings(it) }
        for (consoleViewModel in consoles) {
            val consoleSettings = settings.consolesSettings.firstOrNull { it.getRuntimeId() == consoleViewModel.runtimeId }
            if (consoleSettings == null) {
                settings.adcConsoleSettings(SimpleConsoleConsoleSettingsState(consoleViewModel.runtimeId).apply {
                    consoleViewModel.applyChangesTo(this)
                })
            }
        }
    }

    fun hasConsoleWithId(id: String): Boolean {
        return consoles.any { it.id == id }
    }
}

class SimpleConsoleConsoleProcessorSettingsViewModel(val runtimeId: UUID) :
    ConsoleLogProcessorSettingsViewModel<SimpleConsoleConsoleSettingsState>() {
    var id: String = ""
    var removeAnsiColorCode: Boolean = true
    var supportNesting: Boolean = false
    var displayName: String = "My Awesome Console"
    var parsingMethod: LogParsingMethod = LogParsingMethod.Regex
    var startingLogPattern: String = "^(?<message>.+)$"
    var secondaryLogPattern: String = ""
    var filteringProperties = mutableMapOf<String, String>()
    var environmentVariables = mutableMapOf<String, String>()

    init {
        enableForRun = true
        enableForDebug = true
        readConsoleOutput = true
        readDebugOutput = false
    }

    override fun updateModel(settings: SimpleConsoleConsoleSettingsState) {
        id = settings.id.value
        removeAnsiColorCode = settings.removeAnsiColorCode.value
        supportNesting = settings.supportNesting.value
        displayName = settings.displayName.value
        parsingMethod = settings.parsingMethod.value
        startingLogPattern = settings.startingLogPattern.value
        secondaryLogPattern = settings.secondaryLogPattern.value
        filteringProperties = settings.filteringProperties.toMutableMap()
        environmentVariables = settings.environmentVariables.toMutableMap()
        super.updateModel(settings)
    }

    override fun settingsEquals(settings: SimpleConsoleConsoleSettingsState): Boolean {
        if (id != settings.id.value) return false
        if (removeAnsiColorCode != settings.removeAnsiColorCode.value) return false
        if (supportNesting != settings.supportNesting.value) return false
        if (displayName != settings.displayName.value) return false
        if (parsingMethod != settings.parsingMethod.value) return false
        if (startingLogPattern != settings.startingLogPattern.value) return false
        if (secondaryLogPattern != settings.secondaryLogPattern.value) return false
        if (!filteringProperties.contentEquals(settings.filteringProperties)) return false
        if (!environmentVariables.contentEquals(settings.environmentVariables)) return false
        return super.settingsEquals(settings)

    }

    override fun applyChangesTo(settings: SimpleConsoleConsoleSettingsState) {
        super.applyChangesTo(settings)
        settings.id.set(id)
        settings.removeAnsiColorCode.set(removeAnsiColorCode)
        settings.supportNesting.set(supportNesting)
        settings.displayName.set(displayName)
        settings.parsingMethod.set(parsingMethod)
        settings.startingLogPattern.set(startingLogPattern)
        settings.secondaryLogPattern.set(secondaryLogPattern)
        settings.filteringProperties.clear()
        settings.filteringProperties.putAll(filteringProperties)
        settings.environmentVariables.clear()
        settings.environmentVariables.putAll(environmentVariables)
    }
}

fun Map<String, String>.contentEquals(other: Map<String, String>): Boolean {
    if (size != other.size) return false
    return all { (key, value) -> other[key] == value }
}
