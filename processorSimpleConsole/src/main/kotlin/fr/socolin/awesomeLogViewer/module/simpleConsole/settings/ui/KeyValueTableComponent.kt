package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.ui

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import javax.swing.JComponent

class KeyValueTableComponent(
    keyColumnName: String,
    valueColumnName: String,
    emptyText: String,
) {
    private class Row(var key: String, var value: String)

    private val model = ListTableModel<Row>(
        editableColumn(keyColumnName, { it.key }, { row, value -> row.key = value }),
        editableColumn(valueColumnName, { it.value }, { row, value -> row.value = value }),
    )

    private val table = TableView(model).apply {
        visibleRowCount = 4
        getEmptyText().text = emptyText
    }

    val component: JComponent = ToolbarDecorator.createDecorator(table)
        .setAddAction {
            stopEditing()
            model.addRow(Row("", ""))
            val rowIndex = model.rowCount - 1
            table.selectionModel.setSelectionInterval(rowIndex, rowIndex)
            table.editCellAt(rowIndex, 0)
            table.editorComponent?.requestFocusInWindow()
        }
        .createPanel()

    fun getValues(): MutableMap<String, String> {
        stopEditing()
        val result = linkedMapOf<String, String>()
        for (row in model.items) {
            val key = row.key.trim()
            if (key.isEmpty() || result.containsKey(key))
                continue
            result[key] = row.value.trim()
        }
        return result
    }

    fun setValues(values: Map<String, String>) {
        if (table.isEditing)
            table.cellEditor?.cancelCellEditing()
        model.items = values.map { Row(it.key, it.value) }.toMutableList()
    }

    private fun stopEditing() {
        if (table.isEditing)
            table.cellEditor?.stopCellEditing()
    }

    companion object {
        private fun editableColumn(
            name: String,
            getter: (Row) -> String,
            setter: (Row, String) -> Unit
        ): ColumnInfo<Row, String> {
            return object : ColumnInfo<Row, String>(name) {
                override fun valueOf(item: Row): String = getter(item)
                override fun isCellEditable(item: Row): Boolean = true
                override fun setValue(item: Row, value: String?) = setter(item, value ?: "")
            }
        }
    }
}
