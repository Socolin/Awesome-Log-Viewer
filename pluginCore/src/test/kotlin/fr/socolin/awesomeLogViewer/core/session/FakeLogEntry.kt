package fr.socolin.awesomeLogViewer.core.session

import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel

class FakeLogEntry(
    timeInfo: LogEntryTimeInfo,
    id: String? = null,
    parentId: String? = null,
) : LogEntry(timeInfo, id, parentId) {
    override fun updateRenderModel(
        logSession: LogSession,
        renderModel: LogEntryRenderModel
    ) {
    }

    override fun updateResultCodeRenderModel(
        logSession: LogSession,
        renderModel: ResultCodeRenderModel
    ) {
    }

    override fun getFormattedRenderModel(): FormattedLogModel {
        return FormattedLogModel("Fake Log", null, listOf());
    }

    override fun getFormattedRawLog(): String {
        return "Fake Log"
    }
}
