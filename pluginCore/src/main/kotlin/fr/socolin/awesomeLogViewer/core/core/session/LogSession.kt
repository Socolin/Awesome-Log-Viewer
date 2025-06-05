package fr.socolin.awesomeLogViewer.core.core.session

import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.util.lifetime
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import com.jetbrains.rd.util.reactive.ISource
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.Signal
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.LogSortState
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.SortColumnResult
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.VisibleLogChangeNotifier
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.tool_window.PluginToolWindow
import java.time.Instant
import javax.swing.SwingUtilities


abstract class LogSession(
    val project: Project,
    val pluginSettings: GlobalPluginSettingsStorageService,
    val logProcessor: LogProcessor,
) {
    val startTime: Instant = Instant.now()

    private var uiInitialized = false

    private val lifetimeDefinition = LifetimeDefinition()
    val lifetime = lifetimeDefinition.lifetime

    val changeLogChangeNotifier = VisibleLogChangeNotifier()
    val logSortState = LogSortState()
    val sessionFilter = SessionFilter(
        pluginSettings.state.isFilterCaseSensitive.value,
        getFilteredValuesBySectionFromSettings(logProcessor, pluginSettings)
    )
    val logContainer: LogContainer = createLogContainer()
    val visibleLogCount: Int
        get() = logContainer.visibleLogCount
    val visibleLogsTimeRange: LogTimeRange
        get() = logContainer.logsTimeRange
    val displayMode: LogDisplayMode
        get() = logContainer.displayMode

    val selectedLogIndexUpdated = Signal<Int>()
    val selectedLog = Property<LogEntryDisplay?>(null)
    private val _scrollToLogIndexSignal = Signal<Int>()
    val scrollToLogIndexSignal: ISource<Int>
        get() = _scrollToLogIndexSignal

    init {
        for (filterSectionsDefinition in logProcessor.getFilterSectionsDefinitions()) {
            sessionFilter.addFilterSection(filterSectionsDefinition)
        }
        pluginSettings.state.scrollToEnd.advise(lifetime) {
            if (it) {
                _scrollToLogIndexSignal.fire(logContainer.visibleLogCount - 1)
            }
        }
        pluginSettings.state.isFilterCaseSensitive.advise(lifetime) {
            sessionFilter.setCaseSensitive(it)
        }
        sessionFilter.filterChanged.advise(lifetime) {
            pluginSettings.state.updateFilteredValues(logProcessor.definition.getId(), it.filteredValues)
        }
        logContainer.visibleLogRecomputed.advise(lifetime) {
            val selectedLog = selectedLog.value
            if (selectedLog != null) {
                selectedLogIndexUpdated.fire(logContainer.getVisibleLogIndex(selectedLog))
            }
        }
    }

    abstract fun getRunnerLayoutUi(): RunnerLayoutUi?

    fun supportNesting(): Boolean {
        return logProcessor.supportNesting()
    }

    fun getRawLogLanguage(): Language? = logProcessor.getRawLogLanguage()
    fun getVisibleLogAt(index: Int) = logContainer.getVisibleLogAt(index)
    fun clearLogs() = logContainer.clearLogs()

    fun addLogLine(logEntry: LogEntry) {
        if (!uiInitialized) {
            createUi()
            uiInitialized = true
        }

        sessionFilter.updateFilterValuesWithNewLog(logEntry)

        if (logContainer.addLogEntry(logEntry)) {
            if (pluginSettings.state.scrollToEnd.value) {
                _scrollToLogIndexSignal.fire(logContainer.visibleLogCount - 1)
            }
        }
    }

    fun setTextFilter(value: String) {
        sessionFilter.updateTextFilterValue(value)
    }

    fun selectActiveLogFromIndex(index: Int) {
        val log = logContainer.getVisibleLogAt(index)
        selectedLog.set(log)
    }

    fun scrollToSelectedLog() {
        val selectedLog = selectedLog.value
        if (selectedLog != null) {
            _scrollToLogIndexSignal.fire(logContainer.getVisibleLogIndex(selectedLog))
        }
    }

    fun toggleColumnSort(columnId: String): SortColumnResult {
        return logSortState.toggleColumnSort(columnId)
    }

    private fun createUi() {
        uiInitialized = true
        val runnerLayoutUi: RunnerLayoutUi = getRunnerLayoutUi() ?: return

        SwingUtilities.invokeLater {
            val content = runnerLayoutUi.createContent(
                "log." + logProcessor.definition.getId(),
                PluginToolWindow(this),
                logProcessor.definition.getDisplayName(),
                logProcessor.definition.getIcon(),
                null
            )
            content.lifetime.onTermination {
                lifetimeDefinition.terminate()
                logProcessor.dispose()
            }
            runnerLayoutUi.addContent(content)
        }
    }

    private fun createLogContainer(): LogContainer {
            return FlatLogContainer(
                lifetime,
                sessionFilter,
                pluginSettings,
                changeLogChangeNotifier,
                logSortState
            )
    }

    companion object {
        private fun getFilteredValuesBySectionFromSettings(
            logProcessor: LogProcessor,
            pluginSettings: GlobalPluginSettingsStorageService
        ): Map<String, MutableSet<String>> {
            val prefix = logProcessor.definition.getId() + "."
            val keys = pluginSettings.state.filteredValuesBySectionAndProcessor.keys.filter { it.startsWith(prefix) }.toMutableSet()
            val filteredValuesBySection = mutableMapOf<String, MutableSet<String>>()
            for (key in keys) {
                val filteredValues = pluginSettings.state.filteredValuesBySectionAndProcessor[key]
                if (filteredValues == null) {
                    continue
                }

                val sectionName = key.substring(prefix.length)
                filteredValuesBySection[sectionName] = filteredValues.split(";").toMutableSet()
            }
            return filteredValuesBySection
        }

    }
}
