package fr.socolin.awesomeLogViewer.module.applicationInsights

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.icons.AllIcons
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import fr.socolin.awesomeLogViewer.core.core.utilities.PluginIcons
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.GlobalApplicationInsightsSettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.session.FilterValueDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.session.SessionFilter
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogPropertyModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogSectionModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.addProperties
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.addPropertyIfSet
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsEventTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsExceptionTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsMessageTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsMetricTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsPageViewTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsRemoteDependencyTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsRequestTelemetryData
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.ApplicationInsightsTelemetryData
import java.awt.Color
import kotlin.plus

class ApplicationInsightsLogEntry(
    val type: ApplicationInsightsTelemetryType,
    val data: ApplicationInsightsTelemetryData,
    val configured: Boolean,
    val sampled: Boolean,
    val rawLog: String,
    val tags: Map<String, String>,
    val severityLevel: ApplicationInsightsLogSeverityLevel,
    duration: LogEntryTimeInfo,
    id: String? = null,
    parentId: String? = null,
) : LogEntry(duration, id, parentId) {
    override fun isFiltered(sessionFilter: SessionFilter): Boolean {
        var filtered = sessionFilter.filteredValues["AI_SeverityLevel"]?.contains(severityLevel.toString()) == true
            || sessionFilter.filteredValues["AI_LogType"]?.contains(type.toString()) == true

        if (!filtered) {
            val filterText = sessionFilter.filterText
            if (!filterText.isNullOrBlank()) {
                filtered = !rawLog.contains(filterText, !sessionFilter.caseSensitiveFilter)
            }
        }

        return filtered
    }

    override fun getFilteringParameters(): List<FilterValueDefinition> {
        return listOf(
            FilterValueDefinition("AI_SeverityLevel", severityLevel.toString(), foregroundColor = getSeverityColor()),
            FilterValueDefinition("AI_LogType", type.toString(), getBarColor(), null)
        )
    }

    override fun isSampled(): Boolean {
        return sampled
    }

    fun getSeverityColor(): Color? {
        val pluginSettings = GlobalPluginSettingsStorageService.getInstance()
        return when (severityLevel) {
            ApplicationInsightsLogSeverityLevel.TRACE -> pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Debug]
            ApplicationInsightsLogSeverityLevel.INFO -> pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Info]
            ApplicationInsightsLogSeverityLevel.WARNING -> pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
            ApplicationInsightsLogSeverityLevel.ERROR -> pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
            ApplicationInsightsLogSeverityLevel.CRITICAL -> pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
        }
    }

    override fun getBarColor(): Color? {
        val globalApplicationInsightsSettings = GlobalApplicationInsightsSettingsStorageService.Companion.getInstance().state
        if (data is ApplicationInsightsMessageTelemetryData) {
            return globalApplicationInsightsSettings.messageColor.value
        } else if (data is ApplicationInsightsRequestTelemetryData) {
            return globalApplicationInsightsSettings.requestColor.value
        } else if (data is ApplicationInsightsExceptionTelemetryData) {
            return globalApplicationInsightsSettings.exceptionColor.value
        } else if (data is ApplicationInsightsPageViewTelemetryData) {
            return globalApplicationInsightsSettings.pageViewColor.value
        } else if (data is ApplicationInsightsMetricTelemetryData) {
            return globalApplicationInsightsSettings.metricColor.value
        } else if (data is ApplicationInsightsEventTelemetryData) {
            return globalApplicationInsightsSettings.eventColor.value
        } else if (data is ApplicationInsightsRemoteDependencyTelemetryData) {
            return globalApplicationInsightsSettings.dependencyColor.value
        }
        return null;
    }

    override fun updateRenderModel(
        logSession: LogSession,
        renderModel: LogEntryRenderModel,
    ) {
        renderModel.tooltip = type.toString()

        if (logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
            renderModel.mainLabel.foreground = when (severityLevel) {
                ApplicationInsightsLogSeverityLevel.TRACE -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Debug]
                ApplicationInsightsLogSeverityLevel.INFO -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Info]
                ApplicationInsightsLogSeverityLevel.WARNING -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
                ApplicationInsightsLogSeverityLevel.ERROR -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                ApplicationInsightsLogSeverityLevel.CRITICAL -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
            }
        }

        val logData = data
        if (logData is ApplicationInsightsMessageTelemetryData) {
            renderModel.icon = AllIcons.General.Note
            renderModel.mainLabel.text = logData.message
        } else if (logData is ApplicationInsightsRequestTelemetryData) {
            renderModel.icon = AllIcons.ToolbarDecorator.Import
            renderModel.mainLabel.text = logData.name
            if (!logData.success && logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
                if (logData.responseCode?.startsWith('5') == true || logData.responseCode?.startsWith("Faulted") == true)
                    renderModel.mainLabel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                else if (logData.responseCode?.startsWith('4') == true)
                    renderModel.mainLabel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
            }
        } else if (logData is ApplicationInsightsExceptionTelemetryData) {
            renderModel.icon = AllIcons.Debugger.Db_exception_breakpoint
            renderModel.mainLabel.text = logData.getOuterMostMessage()
            renderModel.tooltip += ": " + logData.getMessageStack().joinToString("<br>  ⤷")
        } else if (logData is ApplicationInsightsPageViewTelemetryData) {
            renderModel.icon = AllIcons.General.InspectionsEye
            renderModel.mainLabel.text = logData.name
        } else if (logData is ApplicationInsightsMetricTelemetryData) {
            renderModel.mainLabel.text = logData.metrics?.joinToString(", ") { it.name + ":" + it.value } ?: ""
            renderModel.icon = PluginIcons.Misc.StatisticsPanel
            renderModel.tooltip += ": " + (logData.metrics?.joinToString("<br>") { it.name + ":" + it.value } ?: "")
        } else if (logData is ApplicationInsightsEventTelemetryData) {
            renderModel.icon = AllIcons.Nodes.Favorite
            renderModel.mainLabel.text = logData.name
        } else if (logData is ApplicationInsightsRemoteDependencyTelemetryData) {
            renderModel.icon = AllIcons.ToolbarDecorator.Export
            renderModel.mainLabel.text = logData.type + " - " + logData.target + " - " + logData.name
            renderModel.tooltip += ": " + logData.data
            if (!logData.success && logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
                if (logData.resultCode?.startsWith('5') == true || logData.resultCode?.startsWith("Faulted") == true)
                    renderModel.mainLabel.foreground =
                        logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                else if (logData.resultCode?.startsWith('4') == true)
                    renderModel.mainLabel.foreground =
                        logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
            }
        }

        renderModel.mainLabel.bold = when (severityLevel) {
            ApplicationInsightsLogSeverityLevel.CRITICAL -> true
            else -> false
        }
    }

    override fun updateResultCodeRenderModel(
        logSession: LogSession,
        renderModel: ResultCodeRenderModel
    ) {
        val logData = data
        if (logData is ApplicationInsightsRequestTelemetryData) {
            renderModel.text = logData.responseCode
            if (!logData.success && logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
                if (logData.responseCode?.startsWith('5') == true || logData.responseCode?.startsWith("Faulted") == true)
                    renderModel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                else if (logData.responseCode?.startsWith('4') == true)
                    renderModel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
            }
        } else if (logData is ApplicationInsightsRemoteDependencyTelemetryData) {
            renderModel.text = logData.resultCode
            if (!logData.success && logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
                if (logData.resultCode?.startsWith('5') == true || logData.resultCode?.startsWith("Faulted") == true)
                    renderModel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                else if (logData.resultCode?.startsWith('4') == true)
                    renderModel.foreground = logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
            }
        }
    }

    override fun getFormattedRenderModel(): FormattedLogModel {
        val sections = mutableListOf<FormattedLogSectionModel>()

        val baseProperties = mutableListOf<FormattedLogPropertyModel>()
        val properties = mutableListOf<FormattedLogPropertyModel>()

        val logData = data
        var stackTrace: String? = null
        if (sampled)
            baseProperties.addPropertyIfSet("Sampled", "true")
        if (!configured)
            baseProperties.addPropertyIfSet("Configured", "AI is not configured to send logs to the server")
        when (logData) {
            is ApplicationInsightsMessageTelemetryData -> {
                baseProperties.addPropertyIfSet("Message", logData.message)
                properties.addProperties(logData.properties)
            }

            is ApplicationInsightsRequestTelemetryData -> {
                baseProperties.addPropertyIfSet("Id", logData.id)
                baseProperties.addPropertyIfSet("Url", logData.url)
                baseProperties.addPropertyIfSet("Name", logData.name)
                baseProperties.addPropertyIfSet("Response Code", logData.responseCode)
                baseProperties.addPropertyIfSet("Success", logData.success.toString())
                properties.addProperties(logData.properties)
            }

            is ApplicationInsightsExceptionTelemetryData -> {
                baseProperties.addPropertyIfSet("Id", logData.id)
                val exceptions = logData.exceptions
                if (exceptions != null) {
                    stackTrace = ""
                    for (exception in exceptions) {
                        stackTrace += exception.asCSharpStack() + "\n"
                        baseProperties.addPropertyIfSet("Message", exception.message)
                    }
                }
                properties.addProperties(logData.properties)
            }

            is ApplicationInsightsPageViewTelemetryData -> {
                baseProperties.addPropertyIfSet("Name", logData.name)
                properties.addProperties(logData.properties)
            }

            is ApplicationInsightsMetricTelemetryData -> {
                val metrics = logData.metrics
                if (metrics != null) {
                    for (metric in metrics) {
                        baseProperties.addPropertyIfSet(metric.name ?: "", metric.value.toString())
                    }
                }
                properties.addProperties(logData.properties)
            }

            is ApplicationInsightsEventTelemetryData -> {
                baseProperties.addPropertyIfSet("Name", logData.name)
                properties.addProperties(logData.properties)
            }

            is ApplicationInsightsRemoteDependencyTelemetryData -> {
                baseProperties.addPropertyIfSet("Id", logData.id)
                baseProperties.addPropertyIfSet("Name", logData.name)
                baseProperties.addPropertyIfSet("Result Code", logData.resultCode)
                baseProperties.addPropertyIfSet("Success", logData.success.toString())
                baseProperties.addPropertyIfSet("Type", logData.type)
                baseProperties.addPropertyIfSet("Target", logData.target)
                baseProperties.addPropertyIfSet("Data", logData.data)
                properties.addProperties(logData.properties)
            }
        }

        sections.add(FormattedLogSectionModel("Data", baseProperties))
        sections.add(
            FormattedLogSectionModel(
                "Tags", tags.map { FormattedLogPropertyModel(it.key, it.value) }.toList()
            )
        )
        sections.add(FormattedLogSectionModel("Properties", properties))

        return FormattedLogModel(type.toString(), stackTrace, sections)
    }

    override fun getFormattedRawLog(): String {
        return prettyPrintGson.toJson(prettyPrintGson.fromJson(rawLog, Object::class.java))
    }

    companion object {
        val prettyPrintGson: Gson = GsonBuilder().setPrettyPrinting().create()
    }
}
