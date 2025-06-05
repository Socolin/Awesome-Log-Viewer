package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import java.awt.Component
import java.time.Duration
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class DurationCellRenderer : JLabel(), TableCellRenderer {
    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        if (table == null) {
            return this
        }

        CellRendererHelper.configureBackgroundBasedOnSelection(this, table, isSelected)

        setValue(value)

        return this
    }

    fun setValue(value: Any?) {
        font = EditorUtil.getEditorFont()
        text = if (value !is Duration)
            null
        else {
            val millis = value.toMillis()
            if (millis == 0L) {
                ""
            } else if (millis < 1_000) {
                " $millis ms"
            } else if (millis < 10_000) {
                String.format(" %.2f", millis / 1000.0) + " s"
            } else if (millis < 100_000) {
                String.format(" %.1f", millis / 1000.0) + " s"
            } else {
                " " + (millis / 1000).toString() + " s"
            }
        }
    }
}
