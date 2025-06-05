package fr.socolin.awesomeLogViewer.module.openTelemetry.console

import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.OpenTelemetryLogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryLogRecordConsoleParser
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryMetricConsoleParser
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryTraceConsoleParser

class OpenTelemetryLogEntryFactory {
    private val openTelemetryTraceConsoleParser = OpenTelemetryTraceConsoleParser()
    private val openTelemetryMetricConsoleParser = OpenTelemetryMetricConsoleParser()
    private val openTelemetryLogRecordConsoleParser = OpenTelemetryLogRecordConsoleParser()

    fun buildLogFromLines(lines: List<String>): LogEntry? {
        val signal = if (lines.first().startsWith("Activity")) {
            openTelemetryTraceConsoleParser.parseTrace(lines)
        } else if (lines.first().startsWith("Metric Name")) {
            openTelemetryMetricConsoleParser.parseMetric(lines)
        } else if (lines.first().startsWith("LogRecord")) {
            openTelemetryLogRecordConsoleParser.parseLogRecord(lines)
        } else {
            null
        }

        if (signal == null) {
            return null
        }

        return OpenTelemetryLogEntry.Companion.createFromSignal(lines.joinToString("\n") { it.trimEnd() }, signal)
    }

}

