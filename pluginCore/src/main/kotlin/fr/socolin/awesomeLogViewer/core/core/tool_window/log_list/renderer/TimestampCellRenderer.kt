package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import org.apache.commons.lang3.time.DurationFormatUtils
import java.awt.Component
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellRenderer

class TimestampCellRenderer(
    val logSession: LogSession
) : JLabel(), TableCellRenderer {
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
        text = if (value is Instant) {
            if (logSession.pluginSettings.state.showTimeFromStart.value) {
                if (logSession.startTime >= value) {
                    "00:00:00.000"
                } else {
                    DurationFormatUtils.formatDuration(Duration.between(logSession.startTime, value).toMillis(), "HH:mm:ss.SSS", true)
                }
            } else {
                formatter.format(value)
            }
        } else {
            null
        }
    }

    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter
            .ofPattern("HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault())
    }
}
