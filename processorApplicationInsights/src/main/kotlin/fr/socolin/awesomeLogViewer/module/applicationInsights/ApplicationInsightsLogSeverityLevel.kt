package fr.socolin.awesomeLogViewer.module.applicationInsights

enum class ApplicationInsightsLogSeverityLevel(val levelName: String) {
    TRACE("Trace"),
    INFO("Info"),
    WARNING("Warning"),
    ERROR("Error"),
    CRITICAL("Critical"),
    ;

    companion object {
        fun fromName(name: String?): ApplicationInsightsLogSeverityLevel? {
            if (name == null)
                return null
            for (value in entries) {
                if (value.levelName == name) {
                    return value
                }
            }
            return null
        }
    }

    override fun toString(): String {
        return levelName
    }
}
