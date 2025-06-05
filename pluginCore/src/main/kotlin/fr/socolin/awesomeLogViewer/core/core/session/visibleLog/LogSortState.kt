package fr.socolin.awesomeLogViewer.core.core.session.visibleLog

import com.jetbrains.rd.util.reactive.Signal

data class SortColumnResult(val previousSortColumnId: String?, val newSortColumnId: String?, val newSortAsc: Boolean?)
class LogSortState {
    val sortChanged = Signal<LogSortState>()
    var sortColumnId: String? = null
    var sortAsc: Boolean? = null

    fun toggleColumnSort(columnId: String): SortColumnResult {
        val previousSortColumnId = sortColumnId
        if (columnId == sortColumnId) {
            if (sortAsc == true) {
                sortAsc = false
            } else {
                sortAsc = null
                sortColumnId = null
            }
        } else {
            sortColumnId = columnId
            sortAsc = true
        }
        sortChanged.fire(this)
        return SortColumnResult(previousSortColumnId, sortColumnId, sortAsc)
    }
}
