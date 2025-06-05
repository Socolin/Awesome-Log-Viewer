package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.core.core.utilities.TimeSpan
import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration
import java.util.HashMap

// FIXME: Mark as Serializable
class ApplicationInsightsRequestTelemetryData : ApplicationInsightsTelemetryData {
    override var id: String? = null

    var name: String? = null
    var duration: String? = null
    var success: Boolean = false
    var responseCode: String? = null
    var url: String? = null
    var properties: HashMap<String, String>? = null

    override fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel? {
        return null;
    }

    override fun getDuration(): Duration? {
        return duration?.let { TimeSpan.parse(it).toDuration() }
    }
}
