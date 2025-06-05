package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

interface ClickableCell {
    fun clickCell(value: Any?, x: Int): Boolean
}
