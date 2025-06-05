package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import java.awt.*
import javax.swing.Box
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

open class LogEntryCellRenderer(
    val logSession: LogSession
) : JPanel(), TableCellRenderer {
    protected val labelMainText = JBLabel()
    protected val icon = JBLabel()

    protected var currentLog: LogEntryDisplay? = null

    protected val renderModel = LogEntryRenderModel(
        null,
        null,
        LabelRenderModel(),
        LabelRenderModel(),
    )

    init {
        labelMainText.font = EditorUtil.getEditorFont()
        border = JBUI.Borders.empty(JBUI.insets(5, 0, 5, 5))
        layout = GridBagLayout()
        add(icon, GridBagConstraints().apply {
            gridx = 4
            gridwidth = 1
            weightx = 0.0
            insets = JBUI.insetsLeft(5)
        })
        add(Box.createRigidArea(Dimension(5, 21)), GridBagConstraints().apply {
            gridx = 5
            gridwidth = 1
            weightx = 0.0
        })
        labelMainText.alignmentX = LEFT_ALIGNMENT
        add(labelMainText, GridBagConstraints().apply {
            gridx = 6
            gridwidth = GridBagConstraints.REMAINDER
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            insets = JBUI.insetsRight(5)
        })
    }

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

        labelMainText.alignmentX = LEFT_ALIGNMENT
        CellRendererHelper.configureBackgroundBasedOnSelection(this, table, isSelected)

        if (value is LogEntryDisplay) {
            setValue(value)
            currentLog = value
        } else {
            currentLog = null
        }

        return this
    }

    open fun setValue(logEntryDisplay: LogEntryDisplay) {
        val editorFont = EditorUtil.getEditorFont()
        renderModel.reset()
        logEntryDisplay.updateRenderModel(logSession, renderModel)

        toolTipText = renderModel.tooltip
        icon.icon = renderModel.icon

        labelMainText.text = renderModel.mainLabel.text
        labelMainText.foreground = renderModel.mainLabel.foreground ?: defaultFontColor
        labelMainText.font = if (renderModel.mainLabel.bold) editorFont.deriveFont(Font.BOLD) else editorFont

        if (logEntryDisplay.logEntry.isSampled() && logSession.pluginSettings.state.showSampledIndicator.value)
            labelMainText.text = "(sampled) " + labelMainText.text

        icon.maximumSize = Dimension(0, 0)
    }

    companion object {
        val defaultFontColor = UIUtil.getLabelFontColor(UIUtil.FontColor.NORMAL)
    }
}

