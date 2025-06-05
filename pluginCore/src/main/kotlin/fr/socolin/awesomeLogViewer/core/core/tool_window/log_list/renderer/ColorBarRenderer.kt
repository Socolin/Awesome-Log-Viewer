package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.util.ui.JBUI
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.border.Border
import javax.swing.table.TableCellRenderer

class ColorBarRenderer() : JPanel(), TableCellRenderer {
    private var barColor: Color? = null

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any,
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
            barColor = value.getBarColor()
        } else {
            barColor = null
        }
        return this
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // FIXME: Color
        g.color = barColor
        g.fillRect(0, 0, 5, height)
    }

    companion object {
        val DEFAULT_NO_FOCUS_BORDER: Border = JBUI.Borders.empty(1)
        val noFocusBorder: Border = DEFAULT_NO_FOCUS_BORDER
    }
}
