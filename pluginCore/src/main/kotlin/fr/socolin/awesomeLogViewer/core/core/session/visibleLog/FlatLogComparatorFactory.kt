package fr.socolin.awesomeLogViewer.core.core.session.visibleLog

import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay

class FlatLogComparatorFactory {
    companion object {
        fun create(
            sortColumnId: String?,
            sortAsc: Boolean?
        ): Comparator<LogEntryDisplay> {
            return when (sortColumnId) {
                "time", "waterfall" -> {
                    getStartTimeComparator(sortAsc ?: true)
                }

                "duration" -> {
                    getDurationTimeComparator(sortAsc ?: true)
                }

                else -> {
                    getDefaultComparator()
                }
            }
        }

        fun getStartTimeComparator(sortAsc: Boolean): Comparator<LogEntryDisplay> {
            val inverter = if (sortAsc) 1 else -1
            return Comparator { log1, log2 ->
                val compareStartDate = log1.logEntry.timeInfo.start.compareTo(log2.logEntry.timeInfo.start)
                if (compareStartDate != 0)
                    return@Comparator compareStartDate * inverter

                return@Comparator log1.id.compareTo(log2.id) * inverter
            }
        }

        fun getDefaultComparator(): Comparator<LogEntryDisplay> {
            return Comparator { log1, log2 ->
                return@Comparator log1.id.compareTo(log2.id)
            }
        }

        fun getDurationTimeComparator(sortAsc: Boolean): Comparator<LogEntryDisplay> {
            val inverter = if (sortAsc) 1 else -1
            return Comparator { log1, log2 ->
                val compareStartDate = log1.logEntry.timeInfo.duration.compareTo(log2.logEntry.timeInfo.duration)
                if (compareStartDate != 0)
                    return@Comparator compareStartDate * inverter

                return@Comparator log1.id.compareTo(log2.id) * inverter
            }
        }
    }
}
