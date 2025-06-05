package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration

interface ApplicationInsightsTelemetryData {
    var id: String?
    fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel?
    fun getDuration(): Duration?
}
