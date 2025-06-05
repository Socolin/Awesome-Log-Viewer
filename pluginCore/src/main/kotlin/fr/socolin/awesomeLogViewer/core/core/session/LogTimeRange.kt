package fr.socolin.awesomeLogViewer.core.core.session

import com.jetbrains.rd.util.reactive.Signal
import java.time.Duration
import java.time.Instant

class LogTimeRange {
    var startDate: Instant = Instant.MAX
    private var endDate: Instant = Instant.MIN
    private var _totalDuration: Duration? = Duration.ZERO
    var totalDuration: Duration
        get() {
            var totalDuration = _totalDuration
            if (totalDuration != null) {
                return totalDuration
            }

            totalDuration = Duration.between(startDate, endDate)
            return totalDuration
        }
        set(value) {
            _totalDuration = value
        }
    val totalDurationUpdated = Signal<Duration>()

    fun reset() {
        startDate = Instant.MAX
        endDate = Instant.MIN
        totalDuration = Duration.ZERO
        totalDurationUpdated.fire(Duration.ZERO)
    }

    fun updateWithLog(logEntry: LogEntry) {
        if (logEntry.timeInfo.start < startDate) {
            startDate = logEntry.timeInfo.start
            totalDurationUpdated.fire(totalDuration)
        }
        if (logEntry.timeInfo.end > endDate) {
            endDate = logEntry.timeInfo.end
            totalDuration = Duration.between(startDate, endDate)
            totalDurationUpdated.fire(totalDuration)
        }
    }
}
