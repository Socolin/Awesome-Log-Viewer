package fr.socolin.awesomeLogViewer.core.core.session.visibleLog

import com.jetbrains.rd.util.reactive.Signal

class VisibleLogChangeNotifier {
    val logAdded = Signal<Pair<Int, Int>>()
    val logRemoved = Signal<Pair<Int, Int>>()
    val logUpdated = Signal<Pair<Int, Int>>()

    fun logAddedAt(index: Int) {
        logAdded.fire(Pair(index, index))
    }

    fun logRemovedAt(index: Int) {
        logRemoved.fire(Pair(index, index))
    }

    fun logRemovedRange(startIndex: Int, endIndex: Int) {
        logRemoved.fire(Pair(startIndex, endIndex))
    }

    fun logUpdatedRange(startIndex: Int, endIndex: Int) {
        logUpdated.fire(Pair(startIndex, endIndex))
    }
}
