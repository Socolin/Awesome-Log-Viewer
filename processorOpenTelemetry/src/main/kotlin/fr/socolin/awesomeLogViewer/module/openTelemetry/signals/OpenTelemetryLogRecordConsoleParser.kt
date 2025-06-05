package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

class OpenTelemetryLogRecordConsoleParser {
    data class PendingOpenTelemetryLogRecord(
        var timestamp: Instant? = null,
        var traceId: String? = null,
        var spanId: String? = null,
        var traceFlags: String? = null,
        var categoryName: String? = null,
        var severity: OpenTelemetrySeverity? = null,
        var severityText: String? = null,
        var formattedMessage: String? = null,
        var body: String? = null,
        var attributes: Map<String, String> = mapOf(),
        var eventId: Int? = null,
        var eventName: String? = null,
        var exception: String? = null,
        var scopeValues: Map<String, String>? = null,
        var resource: Map<String, String> = mapOf()
    ) : OpenTelemetrySignal

    fun parseLogRecord(lines: List<String>): OpenTelemetryLogRecord? {
        val lineIndex = AtomicInteger(0)
        val record = PendingOpenTelemetryLogRecord()

        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            val keyIdentifier = line.substringBefore(":")
            val value = line.substringAfter(": ").trim()

            when (keyIdentifier) {
                "LogRecord.Timestamp" -> record.timestamp = parseTimestamp(value)
                "LogRecord.TraceId" -> record.traceId = value
                "LogRecord.SpanId" -> record.spanId = value
                "LogRecord.TraceFlags" -> record.traceFlags = value
                "LogRecord.CategoryName" -> record.categoryName = value
                "LogRecord.Severity" -> record.severity = OpenTelemetrySeverity.fromSeverityName(value)
                "LogRecord.SeverityText" -> record.severityText = value
                "LogRecord.FormattedMessage" -> record.formattedMessage = value
                "LogRecord.Body" -> record.body = value
                "LogRecord.EventId" -> record.eventId = value.toInt()
                "LogRecord.EventName" -> record.eventName = value
                "LogRecord.Exception" -> record.exception = value
                "LogRecord.Attributes (Key" -> record.attributes = parseKeyValues("    ", lines, lineIndex)
                "\nResource associated with LogRecord" -> record.resource = parseKeyValues("", lines, lineIndex)
            }

            lineIndex.incrementAndGet()
        }

        return OpenTelemetryLogRecord(
            record.timestamp ?: return null,
            record.traceId,
            record.spanId,
            record.traceFlags,
            record.categoryName,
            record.severity,
            record.severityText,
            record.formattedMessage,
            record.body,
            record.attributes,
            record.eventId,
            record.eventName,
            record.exception,
            record.scopeValues,
            record.resource
        )
    }

    private fun parseTimestamp(value: String): Instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value))

    private fun parseKeyValues(
        indent: String,
        lines: List<String>,
        lineIndex: com.jetbrains.rd.util.AtomicInteger
    ): Map<String, String> {
        val tags = mutableMapOf<String, String>()
        lineIndex.incrementAndGet()

        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            if (!line.startsWith(indent)) {
                lineIndex.decrementAndGet()
                break
            }

            val key = line.substringBefore(":").trim()
            val value = line.substringAfter(":").trim()
            tags[key] = value

            lineIndex.incrementAndGet()
        }

        return tags
    }

}
