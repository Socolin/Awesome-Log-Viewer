package fr.socolin.awesomeLogViewer.core.core.log_processor

import com.intellij.lang.Language
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.ConsoleLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.LogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.NetworkLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import com.jetbrains.rd.util.reactive.ISource
import com.jetbrains.rd.util.reactive.Signal

abstract class LogProcessor(
    val definition: LogProcessorDefinition,
    val settings: LogProcessorSettingsState,
) {
    abstract fun getFilterSectionsDefinitions(): List<FilterSectionDefinition>
    abstract fun getRawLogLanguage(): Language?
    abstract fun dispose()
    open fun supportNesting(): Boolean = true
    open fun supportSampling() = false
    open fun getEnvironmentVariables() = emptyMap<String, String>()
}

abstract class ConsoleLogProcessor(
    definition: LogProcessorDefinition,
    settings: ConsoleLogProcessorSettingsState
) : LogProcessor(definition, settings) {
    abstract fun processLogLine(line: String): LogEntry?
    abstract fun shouldListenToDebugOutput(): Boolean
    abstract fun shouldListenToConsoleOutput(): Boolean
}

abstract class NetworkLogProcessor(
    definition: LogProcessorDefinition,
    settings: NetworkLogProcessorSettingsState
) : LogProcessor(definition, settings) {
    protected val notifyLogReceived = Signal<LogEntry>()
    val logReceived: ISource<LogEntry>
        get() = notifyLogReceived

    abstract fun startNetworkCollector()
}
