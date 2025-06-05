package fr.socolin.awesomeLogViewer.core.core.session

import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel
import java.awt.Color
import java.time.Duration
import java.time.Instant

data class LogEntryTimeInfo(val start: Instant, val end: Instant, val duration: Duration) {
    companion object {
        fun createFromStartAndDuration(start: Instant, duration: Duration): LogEntryTimeInfo {
            return LogEntryTimeInfo(start, start.plusMillis(duration.toMillis()), duration)
        }
    }
}

abstract class LogEntry(
    val timeInfo: LogEntryTimeInfo,
    val id: String? = null,
    val parentId: String? = null,
) {
    open fun isFiltered(sessionFilter: SessionFilter): Boolean {
        return false
    }

    open fun getFilteringParameters(): List<FilterValueDefinition> {
        return emptyList()
    }

    open fun isSampled() = false

    abstract fun updateRenderModel(logSession: LogSession, renderModel: LogEntryRenderModel)

    abstract fun updateResultCodeRenderModel(logSession: LogSession, renderModel: ResultCodeRenderModel)

    abstract fun getFormattedRenderModel(): FormattedLogModel

    abstract fun getFormattedRawLog(): String

    open fun getBarColor(): Color? {
        return null
    }
}

class PendingLogEntry(
    timeInfo: LogEntryTimeInfo,
    id: String? = null,
    parentId: String? = null,
) : LogEntry(timeInfo, id, parentId) {
    override fun isFiltered(sessionFilter: SessionFilter): Boolean {
        return true
    }

    override fun updateRenderModel(
        logSession: LogSession,
        renderModel: LogEntryRenderModel
    ) {
        renderModel.mainLabel.text = "..."
    }

    override fun updateResultCodeRenderModel(
        logSession: LogSession,
        renderModel: ResultCodeRenderModel
    ) {
    }

    override fun getFormattedRenderModel(): FormattedLogModel {
        return FormattedLogModel("Pending Log...", null, listOf())
    }

    override fun getFormattedRawLog(): String {
        return ""
    }

}
