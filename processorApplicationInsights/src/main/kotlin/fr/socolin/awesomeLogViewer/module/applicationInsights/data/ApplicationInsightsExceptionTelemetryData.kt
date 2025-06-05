package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration

class ApplicationInsightsExceptionTelemetryData : ApplicationInsightsTelemetryData {
    override var id: String? = null
    override fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel? {
        return ApplicationInsightsLogSeverityLevel.Companion.fromName(severityLevel)
    }

    override fun getDuration(): Duration? {
        return null;
    }

    inner class ExceptionDetailData {
        inner class Stack {
            var level: Int = 0
            var method: String? = null
            var assembly: String? = null
            var fileName: String? = null
            var line: String? = null
        }

        var message: String? = null
        var id: Long = 0
        var typeName: String? = null
        var hasFullStack: Boolean = false
        var parsedStack: ArrayList<Stack>? = null

        fun asCSharpStack(): String {
            val sb = StringBuilder()
            sb.append(typeName).append(": ").append(message).append("\n")
            val parsedStack = parsedStack
            if (parsedStack != null) {
                for (stack in parsedStack) {
                    sb.append("  at ").append(stack.method).append("\n")
                    if (stack.fileName != null) sb.append("    ").append(stack.fileName).append(':').append(stack.line)
                        .append("\n")
                }
            }
            return sb.toString()
        }
    }

    var severityLevel: String? = null
    var exceptions: ArrayList<ExceptionDetailData>? = null
    var properties: HashMap<String, String>? = null

    fun getOuterMostMessage(): String {
        return exceptions?.firstOrNull()?.message ?: ""
    }

    fun getMessageStack(): Collection<String> {
        return exceptions?.map { it.message ?: "" } ?: listOf()
    }
}
