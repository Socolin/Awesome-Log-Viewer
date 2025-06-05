package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage

import com.intellij.util.xmlb.annotations.MapAnnotation
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.Tag
import com.intellij.util.xmlb.annotations.XCollection
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.ConsoleLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsState
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.BooleanPropertyConverter
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.StringPropertyConverter
import fr.socolin.awesomeLogViewer.module.simpleConsole.LogParsingMethod
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.converters.LogParsingMethodPropertyConverter
import com.jetbrains.rd.util.reactive.ISource
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.Signal
import java.util.*

class SimpleConsoleSettingsState : BaseSettingsState() {
    @XCollection(propertyElementName = "consoleSettings", elementName = "consoleSetting", valueAttributeName = "value")
    val consolesSettings = mutableListOf<SimpleConsoleConsoleSettingsState>()

    private val _consoleAdded = Signal<SimpleConsoleConsoleSettingsState>()
    val consoleAdded: ISource<SimpleConsoleConsoleSettingsState>
        get() = _consoleAdded

    private val _consoleRemoved = Signal<SimpleConsoleConsoleSettingsState>()
    val consoleRemoved: ISource<SimpleConsoleConsoleSettingsState>
        get() = _consoleRemoved

    override fun registerProperties() {
        for (state in consolesSettings) {
            registerChildState(state)
        }
    }

    fun adcConsoleSettings(simpleConsoleSettings: SimpleConsoleConsoleSettingsState) {
        consolesSettings.add(simpleConsoleSettings)
        registerChildState(simpleConsoleSettings)
        incModificationCount()
        _consoleAdded.fire(simpleConsoleSettings)
    }

    fun removeConsoleSettings(simpleConsoleSettings: SimpleConsoleConsoleSettingsState) {
        consolesSettings.remove(simpleConsoleSettings)
        incModificationCount()
        _consoleRemoved.fire(simpleConsoleSettings)
    }
}

@Tag("simpleConsoleConsoleSettings")
class SimpleConsoleConsoleSettingsState(runtimeId: UUID? = null) : ConsoleLogProcessorSettingsState() {
    private val _runtimeId: UUID = runtimeId ?: UUID.randomUUID()
    fun getRuntimeId(): UUID = _runtimeId

    @OptionTag("id", converter = StringPropertyConverter::class)
    var id = Property("")

    @OptionTag("DISPLAY_NAME", converter = StringPropertyConverter::class)
    val displayName = Property("Awesome Console")

    @OptionTag("PARSING_METHOD", converter = LogParsingMethodPropertyConverter::class)
    val parsingMethod = Property(LogParsingMethod.Regex)

    @OptionTag("REMOVE_ANSI_COLOR_CODE", converter = BooleanPropertyConverter::class)
    val removeAnsiColorCode = Property(true)

    @OptionTag("SUPPORT_NESTING", converter = BooleanPropertyConverter::class)
    val supportNesting = Property(true)

    @OptionTag("STARTING_LOG_PATTERN", converter = StringPropertyConverter::class)
    val startingLogPattern = Property("^(?<message>.+)$")

    @OptionTag("SECONDARY_LOG_PATTERN", converter = StringPropertyConverter::class)
    val secondaryLogPattern = Property("")

    @MapAnnotation(surroundWithTag = false, surroundKeyWithTag = false, surroundValueWithTag = false)
    val filteringProperties = mutableMapOf<String, String>()

    @MapAnnotation(surroundWithTag = false, surroundKeyWithTag = false, surroundValueWithTag = false)
    val environmentVariables = mutableMapOf<String, String>()

    init {
        enableForRun.set(true)
        enableForDebug.set(true)
        readConsoleOutput.set(true)
        readDebugOutput.set(false)
    }

    override fun registerProperties() {
        super.registerProperties()
        incrementTrackerWhenPropertyChanges(id)
        incrementTrackerWhenPropertyChanges(displayName)
        incrementTrackerWhenPropertyChanges(removeAnsiColorCode)
        incrementTrackerWhenPropertyChanges(startingLogPattern)
        incrementTrackerWhenPropertyChanges(secondaryLogPattern)
        incrementTrackerWhenPropertyChanges(supportNesting)
    }

}
