package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

import java.time.Duration
import java.time.Instant

data class OpenTelemetryTrace(
    val traceId: String?,
    val spanId: String?,
    val activityTraceFlags: ActivityTraceFlags,
    val traceStateString: String? = null,
    val parentSpanId: String? = null,
    val displayName: String,
    val kind: ActivityKind,
    val startTimeUtc: Instant,
    val duration: Duration,
    val status: ActivityStatusCode = ActivityStatusCode.Unset,
    val statusDescription: String? = null,
    val tags: Map<String, String> = emptyMap(),
    val events: List<ActivityEvent> = emptyList(),
    val links: List<ActivityLink> = emptyList(),
    val source: ActivitySource?,
    val resource: Resource? = null
) : OpenTelemetrySignal


enum class ActivityKind {
    Internal,
    Server,
    Client,
    Producer,
    Consumer,
    Unspecified,
    Unknown,
}

enum class ActivityStatusCode {
    Unset,
    Ok,
    Error,
    Unknown
}

enum class ActivityTraceFlags {
    None,
    Recorded,
}

data class ActivityEvent(
    val name: String,
    val timestamp: Instant,
    val attributes: Map<String, String> = emptyMap()
)

data class ActivityLink(
    val context: SpanContext,
    val attributes: Map<String, String> = emptyMap()
)

data class SpanContext(
    val traceId: String,
    val spanId: String
)

data class ActivitySource(
    val name: String,
    val version: String? = null,
    val tags: Map<String, String>? = null
)

data class Resource(
    val attributes: Map<String, String> = emptyMap()
) {
    companion object {
        val EMPTY = Resource()
    }
}
