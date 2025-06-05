package fr.socolin.awesomeLogViewer.module.openTelemetry

import com.intellij.icons.AllIcons
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogPropertyModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogSectionModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.addPropertyIfSet
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import fr.socolin.awesomeLogViewer.core.core.utilities.PluginIcons
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.GlobalOpenTelemetrySettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.session.FilterValueDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.session.SessionFilter
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.ActivityKind
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.ActivityStatusCode
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryLogRecord
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryMetric
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetrySeverity
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetrySignal
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetrySignalType
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.OpenTelemetryTrace
import java.awt.Color
import java.time.Duration

class OpenTelemetryLogEntry(
    val rawLog: String,
    val signal: OpenTelemetrySignal,
    val signalType: OpenTelemetrySignalType,
    duration: LogEntryTimeInfo,
    id: String? = null,
    parentId: String? = null,
) : LogEntry(duration, id, parentId) {
    override fun getFilteringParameters(): List<FilterValueDefinition> {
        return listOf(
            FilterValueDefinition("OT_SignalType", signalType.typeName),
            FilterValueDefinition("OT_Severity", getSeverity().name),
        )
    }

    override fun isFiltered(sessionFilter: SessionFilter): Boolean {
        var filtered = sessionFilter.filteredValues["OT_SignalType"]?.contains(signalType.typeName) == true
            || sessionFilter.filteredValues["OT_Severity"]?.contains(getSeverity().name) == true

        if (!filtered) {
            val filterText = sessionFilter.filterText
            if (!filterText.isNullOrBlank()) {
                var containsFilterText = rawLog.contains(filterText, !sessionFilter.caseSensitiveFilter)
                containsFilterText = containsFilterText || (id?.contains(filterText) == true)
                containsFilterText = containsFilterText || (parentId?.contains(filterText) == true)
                if (signal is OpenTelemetryLogRecord) {
                    containsFilterText = containsFilterText || (signal.spanId?.contains(filterText) == true)
                } else if (signal is OpenTelemetryTrace) {
                    containsFilterText = containsFilterText || signal.spanId?.contains(filterText) == true
                    containsFilterText = containsFilterText || signal.traceId?.contains(filterText) == true
                }
                filtered = !containsFilterText
            }
        }

        return filtered
    }

    override fun getBarColor(): Color? {
        val globalOpenTelemetrySettings = GlobalOpenTelemetrySettingsStorageService.Companion.getInstance().state
        if (signal is OpenTelemetryMetric) {
            return globalOpenTelemetrySettings.metricColor.value
        } else if (signal is OpenTelemetryLogRecord) {
            return globalOpenTelemetrySettings.logRecordColor.value
        } else if (signal is OpenTelemetryTrace) {
            return globalOpenTelemetrySettings.traceColor.value
        }
        return null
    }

    fun getSeverity(): OpenTelemetrySeverity {
        if (signal is OpenTelemetryLogRecord)
            return signal.severity ?: OpenTelemetrySeverity.Unspecified
        return OpenTelemetrySeverity.Unspecified
    }

    override fun updateRenderModel(
        logSession: LogSession,
        renderModel: LogEntryRenderModel,
    ) {
        renderModel.tooltip = signalType.toString()

        if (signal is OpenTelemetryMetric) {
            renderModel.icon = PluginIcons.Misc.StatisticsPanel
            renderModel.mainLabel.text = signal.name
        } else if (signal is OpenTelemetryLogRecord) {
            renderModel.icon = AllIcons.General.Note
            renderModel.mainLabel.text = signal.body
            if (logSession.pluginSettings.state.colorLogBasedOnSeverity.value)
                renderModel.mainLabel.foreground = when (signal.severity) {
                    OpenTelemetrySeverity.Trace -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Trace]
                    OpenTelemetrySeverity.Debug -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Debug]
                    OpenTelemetrySeverity.Info -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Info]
                    OpenTelemetrySeverity.Warn -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
                    OpenTelemetrySeverity.Error -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                    OpenTelemetrySeverity.Fatal -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
                    else -> null
                }
        } else if (signal is OpenTelemetryTrace) {
            if (signal.kind == ActivityKind.Server) {
                renderModel.icon = AllIcons.ToolbarDecorator.Import
                val method = signal.tags["http.request.method"] ?: signal.tags["http.method"]
                val path = signal.tags["url.path"] ?: signal.tags["http.target"]
                val address = signal.tags["server.address"] ?: signal.tags["http.host"]
                renderModel.mainLabel.text = "$method $path - $address"
            } else if (signal.kind == ActivityKind.Client) {
                renderModel.icon = AllIcons.ToolbarDecorator.Export
                renderModel.mainLabel.text = signal.tags["http.request.method"] + " " + signal.tags["url.full"]
            } else {
                renderModel.icon = AllIcons.Toolwindows.ToolWindowHierarchy
                renderModel.mainLabel.text = signal.displayName
            }
            if (signal.status == ActivityStatusCode.Error) {
                renderModel.mainLabel.foreground =
                    logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
            }
        }

    }

    override fun updateResultCodeRenderModel(
        logSession: LogSession,
        renderModel: ResultCodeRenderModel
    ) {
        if (signal is OpenTelemetryTrace) {
            val statusCode = signal.tags["http.response.status_code"] ?: signal.tags["http.status_code"]
            renderModel.text = statusCode
            if (logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
                if (statusCode?.startsWith('5') == true)
                    renderModel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                else if (statusCode?.startsWith('4') == true)
                    renderModel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
            }
        }
    }

    override fun getFormattedRenderModel(): FormattedLogModel {
        val sections = mutableListOf<FormattedLogSectionModel>()

        var stackTrace: String? = null

        if (signal is OpenTelemetryLogRecord) {
            val baseProperties = mutableListOf<FormattedLogPropertyModel>()
            baseProperties.addPropertyIfSet("TraceId", signal.traceId)
            baseProperties.addPropertyIfSet("SpanId", signal.spanId)
            baseProperties.addPropertyIfSet("TraceFlags", signal.traceFlags)
            baseProperties.addPropertyIfSet("CategoryName", signal.categoryName)
            baseProperties.addPropertyIfSet("Severity", signal.severity.toString())
            baseProperties.addPropertyIfSet("SeverityText", signal.severityText)
            baseProperties.addPropertyIfSet("Body", signal.body)
            sections.add(FormattedLogSectionModel("Data", baseProperties))

            stackTrace = signal.exception ?: signal.attributes?.get("exception.stacktrace")

            if (signal.attributes?.isNotEmpty() == true) {
                sections.add(
                    FormattedLogSectionModel(
                        "Attributes",
                        signal.attributes.filter { it.key != "exception.stacktrace" }
                            .map { FormattedLogPropertyModel(it.key, it.value) }.toList()
                    )
                )
            }

            if (signal.resource != null) {
                sections.add(
                    FormattedLogSectionModel(
                        "Resource associated with LogRecord",
                        signal.resource.map { FormattedLogPropertyModel(it.key, it.value) }.toList()
                    )
                )
            }

        } else if (signal is OpenTelemetryMetric) {
            val baseProperties = mutableListOf<FormattedLogPropertyModel>()
            baseProperties.addPropertyIfSet("Name", signal.name)
            sections.add(FormattedLogSectionModel("Data", baseProperties))
        } else if (signal is OpenTelemetryTrace) {
            val baseProperties = mutableListOf<FormattedLogPropertyModel>()
            baseProperties.addPropertyIfSet("Name", signal.displayName)
            baseProperties.addPropertyIfSet("TraceId", signal.traceId)
            baseProperties.addPropertyIfSet("SpanId", signal.spanId)
            baseProperties.addPropertyIfSet("ParentSpanId", signal.parentSpanId)
            baseProperties.addPropertyIfSet("Kind", signal.kind.toString())
            baseProperties.addPropertyIfSet("Status", signal.status.toString())
            sections.add(FormattedLogSectionModel("Data", baseProperties))

            sections.add(
                FormattedLogSectionModel(
                    "Tags", signal.tags.map { FormattedLogPropertyModel(it.key, it.value) }.toList()
                )
            )
            for (link in signal.links) {
                sections.add(
                    FormattedLogSectionModel(
                        "link: " + link.context.spanId + " " + link.context.traceId,
                        link.attributes.map { FormattedLogPropertyModel(it.key, it.value) }.toList()
                    )
                )
            }
            for (event in signal.events) {
                sections.add(
                    FormattedLogSectionModel(
                        "Event: " + event.name,
                        event.attributes.map { FormattedLogPropertyModel(it.key, it.value) }.toList()
                    )
                )
            }
            val resource = signal.resource
            if (resource != null) {
                sections.add(
                    FormattedLogSectionModel(
                        "Associated Resource",
                        resource.attributes.map { FormattedLogPropertyModel(it.key, it.value) }.toList()
                    )
                )
            }
        }


        return FormattedLogModel(signalType.toString(), stackTrace, sections)
    }

    override fun getFormattedRawLog(): String {
        return rawLog
    }

    companion object {
        fun createFromSignal(rawLog: String, signal: OpenTelemetrySignal): OpenTelemetryLogEntry {
            when (signal) {
                is OpenTelemetryLogRecord -> {
                    val logEntryTimeInfo =
                        LogEntryTimeInfo.Companion.createFromStartAndDuration(signal.timestamp, Duration.ZERO)
                    return OpenTelemetryLogEntry(
                        rawLog,
                        signal,
                        OpenTelemetrySignalType.Log,
                        logEntryTimeInfo,
                        null,
                        signal.spanId
                    )
                }

                is OpenTelemetryMetric -> {
                    val startTime = signal.metricPoints.maxBy { it.endTime }.endTime
                    val logEntryTimeInfo =
                        LogEntryTimeInfo.Companion.createFromStartAndDuration(startTime, Duration.ZERO)
                    return OpenTelemetryLogEntry(
                        rawLog,
                        signal,
                        OpenTelemetrySignalType.Metric,
                        logEntryTimeInfo,
                        null,
                        null
                    )
                }

                is OpenTelemetryTrace -> {
                    val logEntryTimeInfo =
                        LogEntryTimeInfo.Companion.createFromStartAndDuration(signal.startTimeUtc, signal.duration)
                    return OpenTelemetryLogEntry(
                        rawLog,
                        signal,
                        OpenTelemetrySignalType.Trace,
                        logEntryTimeInfo,
                        signal.spanId,
                        signal.parentSpanId
                    )
                }

                else -> throw Exception("Unknown open telemetry type")
            }
        }
    }
}
