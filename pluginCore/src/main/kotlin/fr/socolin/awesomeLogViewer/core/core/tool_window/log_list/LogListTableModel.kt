package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list

import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.*
import fr.socolin.awesomeLogViewer.core.core.utilities.debounce
import java.time.Duration
import java.time.Instant
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellRenderer

data class ColumnDefinition<T>(
    val columnId: String,
    val columnName: String,
    val columnType: Class<T>,
    val minWidth: (() -> Int)? = null,
    val preferredWidth: (() -> Int)? = null,
    val maxWidth: (() -> Int)? = null,
    val isSortable: Boolean = false,
    val createCellRenderer: (logSession: LogSession) -> TableCellRenderer?,
    val selector: (LogEntryDisplay) -> T,
)

class LogListTableModel(val logSession: LogSession) : AbstractTableModel() {
    init {
        logSession.changeLogChangeNotifier.logAdded.advise(logSession.lifetime) {
            this.fireTableRowsInserted(it.first, it.second)
        }
        logSession.changeLogChangeNotifier.logRemoved.advise(logSession.lifetime) {
            this.fireTableRowsDeleted(it.first, it.second)
        }
        logSession.changeLogChangeNotifier.logUpdated.advise(logSession.lifetime) {
            this.fireTableRowsUpdated(it.first, it.second)
        }
        logSession.visibleLogsTimeRange.totalDurationUpdated.debounce(300, logSession.lifetime) {
            for (row in 0..<logSession.visibleLogCount) {
                fireTableCellUpdated(row, 3)
            }
        }
    }

    override fun getColumnName(columnIndex: Int): String {
        val columnDefinition = columnDefinitions[columnIndex]
        return columnDefinition.columnName
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        val columnDefinition = columnDefinitions[columnIndex]
        return columnDefinition.columnType
    }

    override fun getRowCount(): Int {
        return logSession.visibleLogCount
    }

    override fun getColumnCount(): Int {
        return columnDefinitions.size
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        if (rowIndex < 0 || rowIndex >= logSession.visibleLogCount)
            return null
        val log = logSession.getVisibleLogAt(rowIndex)
        if (log != null) {
            val columnDefinition = columnDefinitions[columnIndex]
            return columnDefinition.selector.invoke(log)!!
        }
        return null;
    }

    companion object {
        val columnDefinitions: List<ColumnDefinition<*>> = listOf(
            ColumnDefinition(
                "time",
                "Time",
                Instant::class.java,
                minWidth = { -> CellFontHelper.Companion.getIdealColumnSize("HH:mm:ss:SSS ") },
                preferredWidth = { -> CellFontHelper.Companion.getIdealColumnSize("HH:mm:ss:SSS ") },
                maxWidth = { -> CellFontHelper.Companion.getIdealColumnSize("HH:mm:ss:SSS ") },
                isSortable = true,
                createCellRenderer = { TimestampCellRenderer(it) }
            ) { it.logEntry.timeInfo.start },
            ColumnDefinition(
                "colorBar",
                "Color Bar",
                LogEntryDisplay::class.java,
                minWidth = { -> 8 },
                preferredWidth = { -> 8 },
                maxWidth = { -> 8 },
                createCellRenderer = { logSession -> ColorBarRenderer() },
            ) { it },
            ColumnDefinition(
                "severity",
                "Severity",
                LogEntryDisplay::class.java,
                minWidth = { -> CellFontHelper.Companion.getIdealColumnSize(" Inf ") },
                preferredWidth = { -> CellFontHelper.Companion.getIdealColumnSize(" Warning ") },
                maxWidth = { -> CellFontHelper.Companion.getIdealColumnSize(" Warning ") },
                createCellRenderer = { SeverityCellRenderer(it) },
            ) { it },
            ColumnDefinition(
                "message",
                "Message",
                LogEntryDisplay::class.java,
                preferredWidth = { -> 350 },
                createCellRenderer = { logSession ->
                        LogEntryCellRenderer(logSession)
                },
            ) { it },
            ColumnDefinition(
                "result",
                "Result",
                LogEntryDisplay::class.java,
                minWidth = { -> CellFontHelper.Companion.getIdealColumnSize(" 999 ") },
                preferredWidth = { -> CellFontHelper.Companion.getIdealColumnSize(" 999 ") },
                maxWidth = { -> CellFontHelper.Companion.getIdealColumnSize(" Faulted ") },
                createCellRenderer = { ResultCodeCellRenderer(it) },
            ) { it },
            ColumnDefinition(
                "duration",
                "Duration",
                Duration::class.java,
                minWidth = { -> CellFontHelper.Companion.getIdealColumnSize("  123 ms") },
                maxWidth = { -> CellFontHelper.Companion.getIdealColumnSize("  123 ms") },
                isSortable = true,
                createCellRenderer = { DurationCellRenderer() },
            ) { it.logEntry.timeInfo.duration },
        )
    }
}
