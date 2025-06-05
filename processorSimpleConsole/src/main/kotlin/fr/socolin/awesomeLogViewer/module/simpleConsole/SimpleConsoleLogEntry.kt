package fr.socolin.awesomeLogViewer.module.simpleConsole

import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogPropertyModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogSectionModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import fr.socolin.awesomeLogViewer.core.core.session.FilterValueDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.session.SessionFilter

class SimpleConsoleLogEntry(
    val logProcessor: SimpleConsoleLogProcessor,
    var rawLog: String,
    timeInfo: LogEntryTimeInfo,
    id: String? = null,
    parentId: String? = null,
) : LogEntry(timeInfo, id, parentId) {
    val properties = mutableMapOf<String, String>()
    override fun updateRenderModel(
        logSession: LogSession,
        renderModel: LogEntryRenderModel
    ) {
        renderModel.mainLabel.text = properties["message"]

        val severity = properties["severity"]?.lowercase()

        if (logSession.pluginSettings.state.colorLogBasedOnSeverity.value) {
            renderModel.mainLabel.foreground = when (severity) {
                "vrb" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Trace]
                "trace" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Trace]
                "dbug" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Debug]
                "dbg" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Debug]
                "debug" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Debug]
                "info" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Info]
                "inf" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Info]
                "information" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Info]
                "warn" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
                "warning" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Warn]
                "error" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                "fail" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                "err" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                "severe" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Error]
                "crit" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
                "critical" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
                "fatal" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
                "ftl" -> logSession.pluginSettings.state.colorPerSeverity[GenericSeverityLevel.Critical]
                else -> null
            }
        }
    }

    override fun updateResultCodeRenderModel(
        logSession: LogSession,
        renderModel: ResultCodeRenderModel
    ) {
    }

    override fun getFilteringParameters(): List<FilterValueDefinition> {
        val result = mutableListOf<FilterValueDefinition>()
        for (sectionName in logProcessor.filterSectionNames) {
            result.add(FilterValueDefinition(sectionName, properties[sectionName] ?: "None"))
        }
        return result
    }

    override fun isFiltered(sessionFilter: SessionFilter): Boolean {
        val filterText = sessionFilter.filterText
        if (!filterText.isNullOrBlank()) {
            if (!rawLog.contains(filterText, !sessionFilter.caseSensitiveFilter)) {
                return true
            }
        }

        for (sectionName in logProcessor.filterSectionNames) {
            if (sessionFilter.filteredValues[sectionName]?.contains(properties[sectionName] ?: "None") == true)
                return true
        }

        return false
    }

    override fun getFormattedRenderModel(): FormattedLogModel {
        val sections = mutableListOf<FormattedLogSectionModel>()

        val baseProperties = mutableListOf<FormattedLogPropertyModel>()
        for ((key, value) in properties.entries) {
            baseProperties.add(FormattedLogPropertyModel(key, value))
        }
        sections.add(FormattedLogSectionModel("Data", baseProperties))

        return FormattedLogModel("Log", null, sections)
    }

    override fun getFormattedRawLog(): String {
        return rawLog
    }

    fun concatProperties(key: String, value: String) {
        if (properties.containsKey(key)) {
            properties[key] += "\n" + value
        } else {
            properties[key] = value
        }
    }

    fun addRawLogLine(line: String) {
        rawLog += "\n" + line
    }

}
