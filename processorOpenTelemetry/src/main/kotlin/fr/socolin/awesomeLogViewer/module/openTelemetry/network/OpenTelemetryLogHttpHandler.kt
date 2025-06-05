package fr.socolin.awesomeLogViewer.module.openTelemetry.network

import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.OpenTelemetryLogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryLogRecord
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetrySeverity
import com.jetbrains.rd.util.reactive.Signal
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest
import io.opentelemetry.proto.common.v1.AnyValue
import java.time.Instant

class OpenTelemetryLogHttpHandler(private val logReceived: Signal<LogEntry>) : BaseOpenTelemetryHttpHandler() {
    override fun processBytes(bytes: ByteArray) {
        val logRequest = ExportLogsServiceRequest.parseFrom(bytes)
        val resource = logRequest.getResourceLogs(0).resource.attributesList.toMap()
        for (resourceLogs in logRequest.resourceLogsList) {
            for (scopedLog in resourceLogs.scopeLogsList) {
                for (logRecord in scopedLog.logRecordsList) {
                    val trace = OpenTelemetryLogRecord(
                        Instant.ofEpochMilli(logRecord.timeUnixNano / 1_000_000),
                        logRecord.traceId.toHexString(),
                        logRecord.spanId.toHexString(),
                        logRecord.flags.toString(16),
                        scopedLog.scope.name,
                        OpenTelemetrySeverity.Companion.fromSeverityNumber(logRecord.severityNumber.number),
                        logRecord.severityText,
                        null,
                        convertBody(logRecord.body),
                        logRecord.attributesList.toMap(),
                        null,
                        logRecord.eventName,
                        null,
                        scopedLog.scope.attributesList.toMap(),
                        resource,
                    )

                    logReceived.fire(OpenTelemetryLogEntry.Companion.createFromSignal(logRecord.toString(), trace))
                }
            }
        }
    }

    private fun convertBody(body: AnyValue): String {
        if (body.hasStringValue()) {
            return body.stringValue
        }
        return body.toString()
    }
}
