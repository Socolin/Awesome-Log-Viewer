package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.ui.JBColor

data class LabelRenderModel(
    var text: String? = null,
    var foreground: JBColor? = null,
    var bold: Boolean = false,
) {
    fun reset() {
        text = null
        foreground = null
        bold = false
    }
}
