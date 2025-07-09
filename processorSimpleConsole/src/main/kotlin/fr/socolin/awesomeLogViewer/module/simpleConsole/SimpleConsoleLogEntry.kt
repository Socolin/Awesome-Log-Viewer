package fr.socolin.awesomeLogViewer.module.simpleConsole

import fr.socolin.awesomeLogViewer.core.core.session.*
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogPropertyModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.FormattedLogSectionModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.LogEntryRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.ResultCodeRenderModel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer.SeverityRenderModel
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel

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
            val genericSeverityLevel = getGenericSeverity(severity)
            if (genericSeverityLevel != null) {
                renderModel.mainLabel.foreground = logSession.pluginSettings.state.colorPerSeverity[genericSeverityLevel]
            }
        }
    }

    override fun updateResultCodeRenderModel(
        logSession: LogSession,
        renderModel: ResultCodeRenderModel
    ) {
    }

    override fun updateSeverityRenderModel(
        logSession: LogSession,
        renderModel: SeverityRenderModel
    ) {
        val severityLevel = properties["severity"]
        renderModel.severity = getGenericSeverity(severityLevel)
        renderModel.text = severityLevel
    }

    private fun getGenericSeverity(
        severity: String?
    ): GenericSeverityLevel? = when (severity?.lowercase()) {
        "vrb" -> GenericSeverityLevel.Trace
        "trace" -> GenericSeverityLevel.Trace
        "dbug" -> GenericSeverityLevel.Debug
        "dbg" -> GenericSeverityLevel.Debug
        "debug" -> GenericSeverityLevel.Debug
        "info" -> GenericSeverityLevel.Info
        "inf" -> GenericSeverityLevel.Info
        "information" -> GenericSeverityLevel.Info
        "warn" -> GenericSeverityLevel.Warn
        "warning" -> GenericSeverityLevel.Warn
        "error" -> GenericSeverityLevel.Error
        "fail" -> GenericSeverityLevel.Error
        "err" -> GenericSeverityLevel.Error
        "severe" -> GenericSeverityLevel.Error
        "crit" -> GenericSeverityLevel.Critical
        "critical" -> GenericSeverityLevel.Critical
        "fatal" -> GenericSeverityLevel.Critical
        "ftl" -> GenericSeverityLevel.Critical
        else -> null
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
