package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.rd.createNestedDisposable
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ClickableCell
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.SwingUtilities
import javax.swing.table.TableColumn

class LogListComponent(
    private val logSession: LogSession
) : JBPanel<LogListComponent>() {
    private val logTable = JBTable()
    private val logTableModel = LogListTableModel(logSession)

    init {
        layout = GridBagLayout()

        logTable.model = logTableModel
        logTable.setShowGrid(false)
        logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        for ((index, columnDefinition) in LogListTableModel.columnDefinitions.withIndex()) {
            val column = logTable.columnModel.getColumn(index)
            applyColumnConfiguration(column, columnDefinition, logSession)
            column.identifier = columnDefinition.columnId
        }

        logTable.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
        logTable.selectionModel.addListSelectionListener {
            logSession.selectActiveLogFromIndex(logTable.selectedRow)
        }

        logSession.selectedLogIndexUpdated.advise(logSession.lifetime) {
            logTable.selectionModel.setSelectionInterval(it, it)
        }

        logSession.pluginSettings.state.showSampledIndicator.advise(logSession.lifetime) {
            logTableModel.fireTableRowsUpdated(0, logSession.visibleLogCount)
        }
        logSession.pluginSettings.state.showTimeFromStart.advise(logSession.lifetime) {
            logTableModel.fireTableRowsUpdated(0, logSession.visibleLogCount)
        }

        val busConnection = ApplicationManager.getApplication().messageBus.connect(logSession.lifetime.createNestedDisposable())
        busConnection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener { updateColumnSize() })

        // Emulate a button on the 24 first pixel of the row
        logTable.addMouseListener(
            object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e == null) return

                    val row = logTable.rowAtPoint(e.point)
                    val columnIndex = logTable.columnAtPoint(e.point)
                    val columns = logTable.columnModel.columns.toList()
                    val column = columns[columnIndex]

                    val cellRenderer = logTable.getCellRenderer(row, columnIndex)
                    if (cellRenderer is ClickableCell) {
                        var startColumnX = 0
                        for ((index, tableColumn) in columns.withIndex()) {
                            if (index == columnIndex)
                                break
                            startColumnX += tableColumn.width
                        }
                        val distanceFromStart = e.point.x - startColumnX
                        if (cellRenderer.clickCell(logTable.getValueAt(row, columnIndex), distanceFromStart)) {
                            logTableModel.fireTableCellUpdated(row, column.modelIndex)
                        }
                    }
                }
            })

        logTable.tableHeader.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    openColumnSelector(e)
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    openColumnSelector(e)
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                val columnModelIndex = logTable.columnAtPoint(e.point)
                val columnDefinition = LogListTableModel.columnDefinitions.getOrNull(columnModelIndex)
                if (columnDefinition == null) {
                    return
                }
                if (!columnDefinition.isSortable) {
                    return
                }

                val column = logTable.columnModel.getColumn(columnModelIndex)
                val columnId = column.identifier
                if (columnId is String) {
                    val result = logSession.toggleColumnSort(columnId)
                    if (result.previousSortColumnId != null) {
                        updateColumnSortIndicator(result.previousSortColumnId, null)
                    }
                    if (result.newSortColumnId != null) {
                        updateColumnSortIndicator(result.newSortColumnId, result.newSortAsc)
                    }
                }
            }

            private fun updateColumnSortIndicator(
                columnId: String,
                sortAsc: Boolean?,
            ) {
                val column = logTable.columnModel.columns.toList().firstOrNull { it.identifier == columnId } ?: return
                val columnDefinition = LogListTableModel.columnDefinitions[column.modelIndex]
                if (sortAsc == true) {
                    column.headerValue = columnDefinition.columnName + " ↓"
                } else if (sortAsc == false) {
                    column.headerValue = columnDefinition.columnName + " ↑"
                } else {
                    column.headerValue = columnDefinition.columnName
                }
            }
        })

        for (columnId in logSession.pluginSettings.state.getHiddenColumns(logSession.logProcessor.definition.getId())) {
            val columnIndex = LogListTableModel.columnDefinitions.indexOfFirst { it.columnId == columnId }
            if (columnIndex < 0) {
                continue
            }
            val columns = logTable.columnModel.columns.toList()
            val column = columns.firstOrNull { c -> c.modelIndex == columnIndex }
            if (column != null) {
                logTable.removeColumn(column)
            }
        }



        logSession.scrollToLogIndexSignal.advise(logSession.lifetime) {
            performAutoScrollTo(it)
        }

        val scrollPane = JBScrollPane(logTable)
        add(scrollPane, GridBagConstraints().apply {
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        })
    }

    private fun applyColumnConfiguration(
        column: TableColumn,
        columnDefinition: ColumnDefinition<*>,
        logSession: LogSession
    ) {
        column.minWidth = columnDefinition.minWidth?.invoke() ?: column.minWidth
        column.preferredWidth = columnDefinition.preferredWidth?.invoke() ?: column.preferredWidth
        column.maxWidth = columnDefinition.maxWidth?.invoke() ?: column.maxWidth
        column.cellRenderer = columnDefinition.createCellRenderer(logSession)
    }

    private fun updateColumnSize() {
        logTable.setRowHeight(EditorUtil.getEditorFont().size + 18)
        logTable.columnModel.columns.toList().forEach { column ->
            val columnDefinition = LogListTableModel.columnDefinitions[column.modelIndex]
            column.minWidth = columnDefinition.minWidth?.invoke() ?: column.minWidth
            column.preferredWidth = columnDefinition.preferredWidth?.invoke() ?: column.preferredWidth
            column.maxWidth = columnDefinition.maxWidth?.invoke() ?: column.maxWidth
        }
    }

    private fun performAutoScrollTo(index: Int) {
        SwingUtilities.invokeLater {
            val cellRect = logTable.getCellRect(index, 0, true)
            if (!cellRect.isEmpty)
                logTable.scrollRectToVisible(cellRect)
        }
    }

    private fun openColumnSelector(e: MouseEvent) {
        val actionGroup = DefaultActionGroup()

        val activeTabs = logTable.columnModel.columns.toList().map { it.modelIndex }.toSet()
        val filterActions: MutableList<ToggleToolbarAction> = mutableListOf()
        for ((modelIndex, columnDefinition) in LogListTableModel.columnDefinitions.withIndex()) {
            val filterAction = ToggleToolbarAction(
                columnDefinition.columnName,
                activeTabs.contains(modelIndex)
            ) { selected ->
                if (selected) {
                    val column = TableColumn(modelIndex)
                    applyColumnConfiguration(column, columnDefinition, logSession)
                    logTable.addColumn(column)
                    logSession.pluginSettings.state.showColumn(logSession.logProcessor.definition.getId(), columnDefinition.columnId)
                } else {
                    val columns = logTable.columnModel.columns.toList()
                    val column = columns.firstOrNull { c -> c.modelIndex == modelIndex }
                    if (column != null) {
                        logTable.removeColumn(column)
                    }
                    logSession.pluginSettings.state.hideColumn(logSession.logProcessor.definition.getId(), columnDefinition.columnId)
                }
            }
            filterActions.add(filterAction)
            actionGroup.add(filterAction)
        }
        ActionManager.getInstance().createActionPopupMenu("AwesomeLogViewer_ColumnSelector", actionGroup)
            .component
            .show(logTable.tableHeader, e.x, e.y)
    }
}
