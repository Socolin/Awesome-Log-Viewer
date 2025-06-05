package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import javax.swing.JComponent
import javax.swing.JTable

class CellRendererHelper {
    companion object {
        fun configureBackgroundBasedOnSelection(cellRenderer: JComponent, table: JTable, isSelected: Boolean) {
            if (isSelected) {
                cellRenderer.background = table.selectionBackground
                cellRenderer.foreground = table.selectionForeground
            } else {
                cellRenderer.background = table.background
                cellRenderer.foreground = table.foreground
            }
        }
    }
}
