package fr.socolin.awesomeLogViewer.core.core.session

import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.FlatVisibleLogsContainer
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.LogSortState
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.VisibleLogChangeNotifier
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.VisibleLogContainer
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import com.jetbrains.rd.util.AtomicInteger
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.Signal

class FlatLogContainer(
    lifetime: Lifetime,
    private val sessionFilter: SessionFilter,
    pluginSettings: GlobalPluginSettingsStorageService,
    changeLogChangeNotifier: VisibleLogChangeNotifier,
    logSortState: LogSortState,
): LogContainer {
    override val displayMode = LogDisplayMode.Flat

    private val nextLogId = AtomicInteger(1)

    private var maxLogCount = 10_000
    private var currentLogCount = AtomicInteger(0)

    private val logs = ArrayDeque<LogEntryDisplay>()

    private var visibleLogsContainer: VisibleLogContainer<LogEntryDisplay> =
        FlatVisibleLogsContainer(changeLogChangeNotifier, logSortState)
    override val visibleLogCount: Int
        get() = visibleLogsContainer.logCount
    override val logsTimeRange: LogTimeRange
        get() = visibleLogsContainer.logsTimeRange
    override val visibleLogRecomputed = Signal<Boolean>()

    init {
        sessionFilter.filterChanged.advise(lifetime) {
            recomputeVisibleLogs()
        }

        logSortState.sortChanged.advise(lifetime) {
            changeSort(it)
        }

        pluginSettings.state.maxLogCount.advise(lifetime) {
            maxLogCount = it
            removeLogsOverTheLimit()
        }
    }

    override fun getVisibleLogIndex(log: LogEntryDisplay): Int {
        return visibleLogsContainer.indexOf(log)
    }

    override fun getVisibleLogAt(index: Int): LogEntryDisplay? {
        return visibleLogsContainer.getLogAt(index)
    }

    override fun addLogEntry(logEntry: LogEntry): Boolean {
        removeLogsOverTheLimit()

        val log = LogEntryDisplay(nextLogId.andIncrement, logEntry)
        logs.add(log)
        currentLogCount.incrementAndGet()
        log.updateFilterState(sessionFilter)
        if (!log.filtered) {
            visibleLogsContainer.addLog(log)
            return true
        }
        return false
    }

    private fun removeLogsOverTheLimit() {
        while (currentLogCount.get() > maxLogCount && logs.size > 1) {
            val removedLog = logs.removeFirst()
            visibleLogsContainer.removeLogIfExists(removedLog)
            currentLogCount.decrementAndGet()
        }
    }

    override fun clearLogs() {
        logs.clear()
        visibleLogsContainer.clear()
        sessionFilter.resetCounter()
        currentLogCount.set(0)
    }


    fun changeSort(event: LogSortState) {
        visibleLogsContainer.changeSort(event)
        visibleLogRecomputed.fire(true)
    }

    private fun recomputeVisibleLogs() {
        visibleLogsContainer.clear()
        for (log in logs) {
            log.updateFilterState(sessionFilter)
            if (!log.filtered) {
                visibleLogsContainer.addLog(log)
            }
        }

        visibleLogRecomputed.fire(true)
    }
}

