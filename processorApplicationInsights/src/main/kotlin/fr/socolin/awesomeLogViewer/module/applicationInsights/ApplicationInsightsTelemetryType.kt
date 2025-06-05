package fr.socolin.awesomeLogViewer.module.applicationInsights

enum class ApplicationInsightsTelemetryType(private vararg val typeNames: String) {
    Message("Message", "AppTraces"),
    Request("Request", "AppRequests"),
    Exception("Exception", "AppExceptions"),
    PageView("PageViews", "AppPageViews"),
    Metric("Metric", "AppMetrics"),
    Event("Event", "AppEvents"),
    RemoteDependency("RemoteDependency", "AppDependencies"),
    Unk("Unk");

    private val typeName: String = typeNames.first()

    override fun toString(): String {
        return typeName
    }

    companion object {
        fun fromType(typeName: String): ApplicationInsightsTelemetryType {
            val name = typeName.substring(typeName.lastIndexOf('.') + 1)
            for (type in entries) {
                for (n in type.typeNames) {
                    if (name == n) return type
                }
            }
            return Unk
        }
    }
}
