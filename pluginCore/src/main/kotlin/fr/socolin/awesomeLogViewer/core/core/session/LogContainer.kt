package fr.socolin.awesomeLogViewer.core.core.session

import com.jetbrains.rd.util.reactive.Signal

interface LogContainer {
    val visibleLogCount: Int
    val logsTimeRange: LogTimeRange
    val visibleLogRecomputed: Signal<Boolean>
    val displayMode: LogDisplayMode

    fun addLogEntry(logEntry: LogEntry): Boolean
    fun getVisibleLogIndex(log: LogEntryDisplay): Int
    fun getVisibleLogAt(index: Int): LogEntryDisplay?
    fun clearLogs()
}
