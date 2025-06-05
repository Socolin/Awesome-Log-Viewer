package fr.socolin.awesomeLogViewer.core.core.session.visibleLog

import fr.socolin.awesomeLogViewer.core.core.session.LogDisplayMode
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogTimeRange

interface VisibleLogContainer<TLogEntry: LogEntryDisplay> {
    val logCount: Int
    val displayMode: LogDisplayMode
    val logsTimeRange: LogTimeRange
    fun clear()
    fun addLog(log: TLogEntry): Int
    fun removeLog(log: TLogEntry)
    fun removeLogIfExists(log: TLogEntry): Boolean
    fun indexOf(log: TLogEntry): Int
    fun getLogAt(index: Int): TLogEntry?
    fun changeSort(logSortState: LogSortState)
}
