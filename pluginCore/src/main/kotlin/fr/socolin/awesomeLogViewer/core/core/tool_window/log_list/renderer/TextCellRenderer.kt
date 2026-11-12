package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class TextCellRenderer : JLabel(), TableCellRenderer {
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

        font = EditorUtil.getEditorFont()
        val textValue = value as? String
        text = if (textValue.isNullOrEmpty()) "" else " $textValue"
        toolTipText = textValue?.ifEmpty { null }

        return this
    }
}
