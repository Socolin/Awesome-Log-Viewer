package fr.socolin.awesomeLogViewer.module.openTelemetry.console

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorDefinition
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetryConsoleSettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetrySettingsStorageService
import javax.swing.Icon

class OpenTelemetryConsoleLogProcessor(
    definition: LogProcessorDefinition,
    private val openTelemetrySettings: OpenTelemetryConsoleSettingsState
) : ConsoleLogProcessor(definition, openTelemetrySettings) {
    private val pendingLines = mutableListOf<String>()
    private val logEntryFactory = OpenTelemetryLogEntryFactory()

    override fun getFilterSectionsDefinitions(): List<FilterSectionDefinition> {
        return listOf(
            FilterSectionDefinition("OT_SignalType", "Log Type"),
            FilterSectionDefinition("OT_Severity", "Severity Level"),
        )
    }

    override fun getRawLogLanguage(): Language? {
        return PlainTextLanguage.INSTANCE
    }

    override fun dispose() {
    }

    override fun processLogLine(line: String): LogEntry? {
        if (line.isBlank()) {
            return processPendingLines()
        }
        if (line.startsWith("Activity.TraceId") || line.startsWith("LogRecord.Timestamp")) {
            val logEntry = processPendingLines()
            pendingLines.add(line)
            return logEntry
        } else if (line.startsWith("\nMetric Name")) {
            val logEntry = processPendingLines()
            pendingLines.add(line.trimStart())
            return logEntry
        } else {
            pendingLines.add(line)
        }

        return null
    }

    override fun shouldListenToDebugOutput(): Boolean {
        return openTelemetrySettings.readDebugOutput.value
    }

    override fun shouldListenToConsoleOutput(): Boolean {
        return openTelemetrySettings.readConsoleOutput.value
    }

    private fun processPendingLines(): LogEntry? {
        if (pendingLines.isEmpty())
            return null

        val lines = pendingLines.toList()
        pendingLines.clear()
        val logEntry = logEntryFactory.buildLogFromLines(lines)
        return logEntry
    }

    class Definition : LogProcessorDefinition() {
        override fun getId(): String {
            return "openTelemetryConsole"
        }

        override fun getDisplayName(): String {
            return "Open Telemetry (Console)"
        }

        override fun getIcon(): Icon {
            return Icon
        }

        override fun createProcessorIfActive(project: Project, executionMode: ExecutionMode): LogProcessor? {
            val settings = OpenTelemetrySettingsStorageService.Companion.getInstance(project)
            if (!shouldCreateProcessor(settings.state.consoleSettings, executionMode)) {
                return null;
            }
            return OpenTelemetryConsoleLogProcessor(this, settings.state.consoleSettings)
        }
    }

    companion object {
        val Icon: Icon = getIcon("/icons/openTelemetry.svg", OpenTelemetryConsoleLogProcessor::class.java)
    }
}
