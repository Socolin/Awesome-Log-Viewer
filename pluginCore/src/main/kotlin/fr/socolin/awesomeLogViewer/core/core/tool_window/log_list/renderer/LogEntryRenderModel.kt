package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import javax.swing.Icon

class LogEntryRenderModel(
    var icon: Icon?,
    var tooltip: String? = null,
    val mainLabel: LabelRenderModel,
    val extraLabel: LabelRenderModel,
) {
    fun reset() {
        icon = null
        tooltip = null
        mainLabel.reset()
        extraLabel.reset()
    }
}
