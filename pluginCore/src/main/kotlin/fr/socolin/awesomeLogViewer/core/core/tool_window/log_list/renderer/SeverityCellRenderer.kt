package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.ui.JBColor
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import org.apache.velocity.runtime.directive.contrib.For
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class SeverityCellRenderer(
    val logSession: LogSession
) : JLabel(), TableCellRenderer {
    private val renderModel = SeverityRenderModel()

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

        logEntryDisplay.updateSeverityRenderModel(logSession, renderModel)
        if (renderModel.text != null)
            text = renderModel.text
        else
            text = null
        val severityLevel = renderModel.severity
        if (severityLevel != null) {
            foreground = logSession.pluginSettings.state.colorPerSeverity[severityLevel]
        } else {
            foreground = null
        }
    }
}

class SeverityRenderModel(
    var text: String? = null,
    var severity: GenericSeverityLevel? = null,
) {
    fun reset() {
        text = null
        severity = null
    }
}
