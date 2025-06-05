package fr.socolin.awesomeLogViewer.module.applicationInsights.data

import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogSeverityLevel
import java.time.Duration
import java.util.HashMap

class ApplicationInsightsMetricTelemetryData : ApplicationInsightsTelemetryData {
    override var id: String? = null
    override fun getSeverityLevel(): ApplicationInsightsLogSeverityLevel? {
        return null
    }

    class Metric {
        var name: String? = null
        var kind: String? = null
        var value: Int = 0
        var count: Int = 0
    }


    override fun getDuration(): Duration? {
        return null;
    }

    var metrics: ArrayList<Metric>? = null
    var properties: HashMap<String, String>? = null
}
