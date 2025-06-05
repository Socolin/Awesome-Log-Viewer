package fr.socolin.awesomeLogViewer.core.core.session.visibleLog

import fr.socolin.awesomeLogViewer.core.core.session.LogDisplayMode
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogTimeRange

class FlatVisibleLogsContainer<TLogEntry: LogEntryDisplay>(
    val logChangeNotifier: VisibleLogChangeNotifier,
    logSortState: LogSortState,
) : VisibleLogContainer<TLogEntry> {
    private var comparator: Comparator<LogEntryDisplay> = FlatLogComparatorFactory.create(logSortState.sortColumnId, logSortState.sortAsc)
    override val logCount: Int
        get() = logs.size

    override val displayMode = LogDisplayMode.Flat

    private val logs = arrayListOf<TLogEntry>()
    override val logsTimeRange = LogTimeRange()

    override fun indexOf(log: TLogEntry): Int {
        return logs.binarySearch(log, comparator)
    }

    override fun getLogAt(index: Int): TLogEntry? {
        return logs.getOrNull(index)
    }

    override fun changeSort(logSortState: LogSortState) {
        this.comparator = FlatLogComparatorFactory.create(logSortState.sortColumnId, logSortState.sortAsc)
        logs.sortWith(comparator)
        logChangeNotifier.logUpdatedRange(0, logs.size)
    }

    override fun addLog(log: TLogEntry): Int {
        val index = logs.binarySearch(log, comparator)
        if (index < 0) {
            val insertIndex = -index - 1
            logs.add(insertIndex, log)
            logChangeNotifier.logAddedAt(insertIndex)
            logsTimeRange.updateWithLog(log.logEntry)
            return insertIndex
        } else {
            // Element is already present, something is wrong
            throw Error("The log is already present in the sorted log container $log")
        }
    }

    override fun removeLog(log: TLogEntry) {
        if (!removeLogIfExists(log)) {
            throw Error("The log was not found: $log")
        }
    }

    override fun removeLogIfExists(log: TLogEntry): Boolean {
        val removeIndex = logs.binarySearch(log, comparator)
        if (removeIndex < 0) {
            return false
        }

        val removed = logs.removeAt(removeIndex)
        assert(removed == log)
        logChangeNotifier.logRemovedAt(removeIndex)
        return true
    }

    override fun clear() {
        logsTimeRange.reset()
        logChangeNotifier.logRemovedRange(0, logs.size)
        logs.clear()
    }
}
