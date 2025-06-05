package fr.socolin.awesomeLogViewer.core.core.utilities

import java.time.Duration

class TimeSpan(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val milliseconds: Int
) : Comparable<TimeSpan?> {
    val total: Long

    init {
        total = hours * 3600000L + minutes * 60000L + seconds * 1000L + milliseconds
    }

    fun toDuration(): Duration {
        return Duration.ofMillis(total)
    }

    override fun toString(): String {
        if (hours == 0) {
            if (minutes == 0) {
                if (seconds == 0) {
                    if (milliseconds == 0) return ""
                    return milliseconds.toString() + "ms"
                }
                return seconds.toString() + "." + milliseconds + "s"
            }
            return minutes.toString() + "m " + seconds + "." + milliseconds + "s"
        }
        return hours.toString() + "h " + minutes + "m " + seconds + "." + milliseconds + "s"
    }

    override fun compareTo(other: TimeSpan?): Int {
        if (other == null) {
            return -1
        }
        return (other.total - total).toInt()
    }

    companion object {
        fun parse(durationString: String): TimeSpan {
            val split = durationString.split("\\.".toRegex(), limit = 2).toTypedArray()
            val hourMinuteSeconds = split[0].split(":".toRegex(), limit = 3).toTypedArray()
            val hours = hourMinuteSeconds[0].toInt()
            val minutes = hourMinuteSeconds[1].toInt()
            val seconds = hourMinuteSeconds[2].toInt()
            val milliseconds = if (split.size > 1) split[1].substring(0, 3).toInt() else 0

            return TimeSpan(hours, minutes, seconds, milliseconds)
        }

        var Zero: TimeSpan = TimeSpan(0, 0, 0, 0)
    }
}



