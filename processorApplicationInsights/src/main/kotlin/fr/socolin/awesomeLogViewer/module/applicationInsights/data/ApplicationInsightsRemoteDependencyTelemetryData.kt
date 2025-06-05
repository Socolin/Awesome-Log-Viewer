package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.core.core.utilities.TimeSpan
import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration
import java.util.HashMap

class ApplicationInsightsRemoteDependencyTelemetryData : ApplicationInsightsTelemetryData {
    override var id: String? = null

    override fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel? {
        return null
    }

    override fun getDuration(): Duration? {
        return duration?.let { TimeSpan.parse(it).toDuration() }
    }

    var name: String? = null
    var duration: String? = null
    var resultCode: String? = null
    var data: String? = null
    var success: Boolean = false
    var type: String? = null
    var target: String? = null
    var properties: HashMap<String, String>? = null
}
