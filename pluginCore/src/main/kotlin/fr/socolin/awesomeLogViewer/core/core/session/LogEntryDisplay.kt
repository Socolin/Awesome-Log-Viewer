package fr.socolin.awesomeLogViewer.core.core.session

import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.SeverityRenderModel
import java.awt.Color

open class LogEntryDisplay(
    val id: Int,
    val logEntry: LogEntry
) {
    var filtered = false

    var isLastVisibleChild: Boolean = false
    var isLastVisibleChildEtag: Int = 0

    fun updateRenderModel(logSession: LogSession, renderModel: LogEntryRenderModel) = logEntry.updateRenderModel(logSession, renderModel)
    fun updateResultCodeRenderModel(logSession: LogSession, renderModel: ResultCodeRenderModel) = logEntry.updateResultCodeRenderModel(logSession, renderModel)
    fun updateSeverityRenderModel(logSession: LogSession, renderModel: SeverityRenderModel) = logEntry.updateSeverityRenderModel(logSession, renderModel)
    fun getBarColor(): Color? = logEntry.getBarColor()
    fun getFormattedRenderModel(): FormattedLogModel = logEntry.getFormattedRenderModel()

    open fun updateFilterState(sessionFilter: SessionFilter): Boolean {
        filtered = logEntry.isFiltered(sessionFilter)
        return filtered
    }

    override fun toString(): String {
        return "logEntryDisplay[id=${id}] logEntry=${logEntry.id}"
    }
}

