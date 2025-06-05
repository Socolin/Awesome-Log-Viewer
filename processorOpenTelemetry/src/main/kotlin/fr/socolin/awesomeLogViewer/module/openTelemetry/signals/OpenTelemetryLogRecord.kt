package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

import java.time.Instant

data class OpenTelemetryLogRecord(
    val timestamp: Instant,
    val traceId: String?,
    val spanId: String?,
    val traceFlags: String?,
    val categoryName: String?,
    val severity: OpenTelemetrySeverity?,
    val severityText: String?,
    val formattedMessage: String?,
    val body: String?,
    val attributes: Map<String, String>?,
    val eventId: Int?,
    val eventName: String?,
    val exception: String?,
    val scopeValues: Map<String, String>?,
    val resource: Map<String, String>?
) : OpenTelemetrySignal

data class Range(val from: Int, val to: Int)

enum class OpenTelemetrySeverity(val severityNumberRange: Range) {
    Unspecified(Range(0, 0)),
    Trace(Range(1, 4)),
    Debug(Range(5, 8)),
    Info(Range(9, 12)),
    Warn(Range(13, 16)),
    Error(Range(17, 20)),
    Fatal(Range(21, 24));

    companion object {
        fun fromSeverityName(severityText: String): OpenTelemetrySeverity? {
            for (value in entries) {
                if (value.name.equals(severityText, ignoreCase = true)) {
                    return value
                }
            }
            return null
        }

        fun fromSeverityNumber(severityNumber: Int): OpenTelemetrySeverity? = entries.firstOrNull {
            it.severityNumberRange.from <= severityNumber && severityNumber <= it.severityNumberRange.to
        }
    }
}
