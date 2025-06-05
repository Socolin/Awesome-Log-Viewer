package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration

class ApplicationInsightsEventTelemetryData : ApplicationInsightsTelemetryData {
    override var id: String? = null
    override fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel? {
        return null;
    }

    override fun getDuration(): Duration? {
        return null;
    }

    var name: String? = null
    var properties: HashMap<String, String>? = null
}
