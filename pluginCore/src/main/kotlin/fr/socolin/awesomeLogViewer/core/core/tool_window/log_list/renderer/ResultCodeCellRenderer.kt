package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.ui.JBColor
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer


class ResultCodeCellRenderer(
    val logSession: LogSession
) : JLabel(), TableCellRenderer {
    private val renderModel = ResultCodeRenderModel()

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

        if (value is LogEntryDisplay) {
            setValue(value)
        }

        return this
    }

    fun setValue(logEntryDisplay: LogEntryDisplay) {
        renderModel.reset()
        font = EditorUtil.getEditorFont()

        logEntryDisplay.updateResultCodeRenderModel(logSession, renderModel)
        if (renderModel.text != null)
            text = " " + renderModel.text
        else
            text = null
        foreground = renderModel.foreground
    }
}

class ResultCodeRenderModel(
    var text: String? = null,
    var foreground: JBColor? = null,
) {
    fun reset() {
        text = null
        foreground = null
    }
}
