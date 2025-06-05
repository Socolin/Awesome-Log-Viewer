package fr.socolin.awesomeLogViewer.module.applicationInsights.console

import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorDefinition
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogEntryFactory
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsConsoleSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsSettingsStorageService
import javax.swing.Icon

private val LOG = logger<ApplicationInsightsConsoleLogProcessor>()

class ApplicationInsightsConsoleLogProcessor(
    definition: LogProcessorDefinition,
    private val applicationInsightsSettings: ApplicationInsightsConsoleSettingsState
) : ConsoleLogProcessor(definition, applicationInsightsSettings) {
    val appInsightsLogPrefix: String = "category: Application Insights Telemetry"
    val filteredByPrefix: String = " (filtered by "
    val unconfiguredPrefix: String = " (unconfigured) "

    override fun getRawLogLanguage(): Language? {
        return JsonLanguage.INSTANCE
    }

    override fun supportSampling(): Boolean {
        return true
    }

    override fun processLogLine(line: String): LogEntry? {
        if (!line.startsWith(appInsightsLogPrefix)) {
            return null
        }

        val startJsonIndex = line.indexOf('{')
        if (startJsonIndex == -1) {
            return null
        }
        val endJsonIndex = line.lastIndexOf('}')
        if (endJsonIndex <= startJsonIndex) {
            return null
        }

        val json = line.substring(startJsonIndex, endJsonIndex + 1)

        var filtered = false
        var configured = true
        val telemetryState: String = line.substring(appInsightsLogPrefix.length)
        if (telemetryState.startsWith(filteredByPrefix)) {
            filtered = true
        } else if (telemetryState.startsWith(unconfiguredPrefix)) {
            configured = false
        }

        try {
            return ApplicationInsightsLogEntryFactory.Companion.createFromJson(json, filtered, configured)
        } catch (e: Exception) {
            LOG.warn("Failed to parse Application insights log entry json: $json", e)
        }
        return null
    }

    override fun shouldListenToDebugOutput(): Boolean {
        return applicationInsightsSettings.readDebugOutput.value
    }

    override fun shouldListenToConsoleOutput(): Boolean {
        return applicationInsightsSettings.readConsoleOutput.value
    }

    override fun getFilterSectionsDefinitions(): List<FilterSectionDefinition> {
        return listOf(
            FilterSectionDefinition("AI_SeverityLevel", "Severity Level"),
            FilterSectionDefinition("AI_LogType", "Log Type"),
        )
    }

    override fun dispose() {
    }

    class Definition : LogProcessorDefinition() {
        override fun getId(): String {
            return "applicationInsightsConsole"
        }

        override fun getDisplayName(): String {
            return "Application Insights (Console)"
        }

        override fun getIcon(): Icon {
            return Icon
        }

        override fun createProcessorIfActive(
            project: Project,
            executionMode: ExecutionMode,
        ): LogProcessor? {
            val settings = ApplicationInsightsSettingsStorageService.Companion.getInstance(project)
            if (!shouldCreateProcessor(settings.state.consoleSettings, executionMode)) {
                return null;
            }
            return ApplicationInsightsConsoleLogProcessor(this, settings.state.consoleSettings)
        }
    }

    companion object {
        val Icon: Icon = getIcon("/icons/application-insights.svg", ApplicationInsightsConsoleLogProcessor::class.java)
    }
}


