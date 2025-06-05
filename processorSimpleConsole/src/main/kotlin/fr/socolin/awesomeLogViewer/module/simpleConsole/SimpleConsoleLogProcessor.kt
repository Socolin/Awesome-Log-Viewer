package fr.socolin.awesomeLogViewer.module.simpleConsole

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.util.containers.toArray
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorDefinition
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleConsoleSettingsState
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleSettingsStorageService
import java.time.Duration
import java.time.Instant
import java.util.regex.Pattern
import javax.swing.Icon
import kotlin.text.get

private val LOG = logger<SimpleConsoleLogProcessor>()

class SimpleConsoleLogProcessor(
    definition: LogProcessorDefinition,
    private val simpleConsoleSettings: SimpleConsoleConsoleSettingsState
) : ConsoleLogProcessor(definition, simpleConsoleSettings) {

    val lineMatchingPattern: Pattern = Pattern.compile(simpleConsoleSettings.startingLogPattern.value)
    val lineLinePatternGroupNames = lineMatchingPattern.namedGroups().keys.toList()
    val lineMatchingRegex = lineMatchingPattern.toRegex()

    val secondaryLinePattern: Pattern = Pattern.compile(simpleConsoleSettings.secondaryLogPattern.value)
    val secondaryLinePatternGroupNames = secondaryLinePattern.namedGroups().keys.toList()
    val secondaryLineRegex = secondaryLinePattern.toRegex()

    val filteringProperties = simpleConsoleSettings.filteringProperties.toMap()
    val filterSectionNames = simpleConsoleSettings.filteringProperties.keys.toArray(arrayOf())

    var lastLog: SimpleConsoleLogEntry? = null

    override fun processLogLine(line: String): LogEntry? {
        try {
            if (simpleConsoleSettings.parsingMethod.value == LogParsingMethod.JSON) {
                val jsonLog = JsonParser.parseString(line).asJsonObject
                val id = readStringFrom(jsonLog, "id")
                val parentId = readStringFrom(jsonLog, "parentId")
                val logEntry = SimpleConsoleLogEntry(
                    this,
                    prettyPrintGson.toJson(prettyPrintGson.fromJson(line, Object::class.java)),
                    LogEntryTimeInfo.createFromStartAndDuration(Instant.now(), Duration.ZERO),
                    id,
                    parentId
                )
                for (key in jsonLog.keySet()) {
                    val jsonElement = jsonLog.get(key)
                    if (jsonElement.isJsonPrimitive) {
                        if (jsonElement.asJsonPrimitive.isString) {
                            logEntry.properties[key] = jsonElement.asString
                        } else if (jsonElement.asJsonPrimitive.isNumber) {
                            logEntry.properties[key] = jsonElement.asNumber.toString()
                        } else if (jsonElement.asJsonPrimitive.isBoolean) {
                            logEntry.properties[key] = jsonElement.asBoolean.toString()
                        }
                    }
                }
                return logEntry
            } else if (simpleConsoleSettings.parsingMethod.value == LogParsingMethod.Regex) {
                var cleanLine = line
                if (simpleConsoleSettings.removeAnsiColorCode.value) {
                    cleanLine = AnsiCodeUtil.removeAnsiCodes(line).trimEnd()
                }

                // (?<date>\d+-\d+-\d+\s\d+:+\d+:\d+),(?<ukn>(\d+))\s\[\s*(?<threadId>\d+)\]\s*(?<severity>\w+)\s*-\s*(?<message>.+)
                val matchResult = lineMatchingRegex.matchEntire(cleanLine)
                if (matchResult != null) {
                    // FIXME: Parse date and duration if available
                    val logEntry =
                        SimpleConsoleLogEntry(
                            this,
                            cleanLine,
                            LogEntryTimeInfo.createFromStartAndDuration(Instant.now(), Duration.ZERO),
                            if (lineLinePatternGroupNames.contains("id")) matchResult.groups["id"]?.value else null,
                            if (lineLinePatternGroupNames.contains("parentId")) matchResult.groups["parentId"]?.value else null,
                        )
                    addCaptureToLogProperties(logEntry, matchResult, lineLinePatternGroupNames)
                    lastLog = logEntry
                    return logEntry
                }

                val lastLogEntry = lastLog
                if (lastLogEntry != null) {
                    val matchResult = secondaryLineRegex.matchEntire(cleanLine)
                    if (matchResult != null) {
                        addCaptureToLogProperties(lastLogEntry, matchResult, secondaryLinePatternGroupNames)
                        lastLogEntry.addRawLogLine(cleanLine)
                    } else {
                        lastLog = null
                    }
                }
            }
        } catch (_: Exception) {
            return null
        }

        return null
    }

    override fun shouldListenToDebugOutput(): Boolean {
        return simpleConsoleSettings.readDebugOutput.value
    }

    override fun shouldListenToConsoleOutput(): Boolean {
        return simpleConsoleSettings.readConsoleOutput.value
    }

    private fun addCaptureToLogProperties(
        logEntry: SimpleConsoleLogEntry,
        matchResult: MatchResult,
        propertyNames: List<String>,
    ) {
        for (propertyName in propertyNames) {
            val value = matchResult.groups[propertyName]?.value
            if (value != null) {
                logEntry.concatProperties(propertyName, value)
            }
        }
    }

    override fun getFilterSectionsDefinitions(): List<FilterSectionDefinition> {
        return filteringProperties
            .map { FilterSectionDefinition(it.key, it.value) }
            .toList()
    }

    override fun getEnvironmentVariables(): Map<String, String> {
        return simpleConsoleSettings.environmentVariables
    }

    override fun getRawLogLanguage(): Language? {
        return PlainTextLanguage.INSTANCE
    }

    override fun dispose() {
    }

    override fun supportNesting(): Boolean {
        return simpleConsoleSettings.supportNesting.value
    }

    private fun readStringFrom(jsonObject: JsonObject, key: String): String? {
        if (!jsonObject.has(key)) {
            return null
        }
        val jsonElement = jsonObject.get(key)
        if (jsonElement.isJsonNull) {
            return null
        }
        if (!jsonElement.isJsonPrimitive) {
            return null
        }
        val jsonPrimitive = jsonElement.asJsonPrimitive
        if (jsonPrimitive.isString) {
            return jsonPrimitive.asString
        }
        if (jsonPrimitive.isNumber) {
            return jsonPrimitive.asNumber.toString()
        }
        return jsonPrimitive.toString()
    }

    class Definition(
        val processorId: String,
        val processorDisplayName: String,
    ) : LogProcessorDefinition() {
        override fun getId(): String {
            return processorId
        }

        override fun getDisplayName(): String {
            return processorDisplayName
        }

        override fun getIcon(): Icon {
            return AllIcons.Nodes.Console
        }

        override fun createProcessorIfActive(project: Project, executionMode: ExecutionMode): LogProcessor? {
            val settings = SimpleConsoleSettingsStorageService.Companion.getInstance(project)
            val simpleConsoleSettings = settings.state.consolesSettings.firstOrNull { buildId(it) == getId() }
            if (simpleConsoleSettings == null) {
                LOG.warn(
                    "Failed to create simple console log processor, no settings found for id: ${getId()} existing ids: ${
                        settings.state.consolesSettings.joinToString(",") { buildId(it) }
                    }}")
                return null
            }
            if (!shouldCreateProcessor(simpleConsoleSettings, executionMode)) {
                return null
            }
            return SimpleConsoleLogProcessor(this, simpleConsoleSettings)
        }

        companion object {
            fun buildId(settings: SimpleConsoleConsoleSettingsState): String {
                return "SimpleConsole" + settings.id.value
            }

            fun fromSettings(settings: SimpleConsoleConsoleSettingsState): Definition {
                return Definition(buildId(settings), settings.displayName.value)
            }
        }
    }

    companion object {
        val prettyPrintGson: Gson = GsonBuilder().setPrettyPrinting().create()
    }
}

