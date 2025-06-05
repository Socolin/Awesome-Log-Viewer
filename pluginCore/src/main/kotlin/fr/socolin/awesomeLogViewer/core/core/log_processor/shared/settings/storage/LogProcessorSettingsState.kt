package fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage

import com.intellij.util.xmlb.annotations.OptionTag
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsState
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.BooleanPropertyConverter
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.IntPropertyConverter
import com.jetbrains.rd.util.reactive.Property

abstract class LogProcessorSettingsState : BaseSettingsState() {
    @OptionTag("ENABLE_FOR_RUN", converter = BooleanPropertyConverter::class)
    val enableForRun = Property(true)

    @OptionTag("ENABLE_FOR_DEBUG", converter = BooleanPropertyConverter::class)
    val enableForDebug = Property(true)

    override fun registerProperties() {
        incrementTrackerWhenPropertyChanges(enableForRun)
        incrementTrackerWhenPropertyChanges(enableForDebug)
    }
}

abstract class ConsoleLogProcessorSettingsState : LogProcessorSettingsState() {
    @OptionTag("READ_DEBUG_OUTPUT", converter = BooleanPropertyConverter::class)
    val readDebugOutput = Property(true)

    @OptionTag("READ_CONSOLE_OUTPUT", converter = BooleanPropertyConverter::class)
    val readConsoleOutput = Property(true)

    override fun registerProperties() {
        super.registerProperties()
        incrementTrackerWhenPropertyChanges(readDebugOutput)
        incrementTrackerWhenPropertyChanges(readConsoleOutput)
    }
}

abstract class NetworkLogProcessorSettingsState : LogProcessorSettingsState() {
    // 0 = Random port
    @OptionTag("LISTEN_PORT_NUMBER", converter = IntPropertyConverter::class)
    val listenPortNumber = Property(0)

    override fun registerProperties() {
        super.registerProperties()
        incrementTrackerWhenPropertyChanges(listenPortNumber)
    }
}
