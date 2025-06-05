package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration
import java.util.HashMap

class ApplicationInsightsMessageTelemetryData : ApplicationInsightsTelemetryData {
    override var id: String? = null
    override fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel? {
        return ApplicationInsightsLogSeverityLevel.Companion.fromName(severityLevel)
    }

    override fun getDuration(): Duration? {
        return null;
    }

    var severityLevel: String? = null
    var message: String? = null
    var properties: HashMap<String, String>? = null
}
