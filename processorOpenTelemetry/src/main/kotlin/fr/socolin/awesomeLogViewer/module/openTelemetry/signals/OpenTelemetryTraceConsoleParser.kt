package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

import fr.socolin.awesomeLogViewer.core.core.utilities.TimeSpan
import com.jetbrains.rd.util.AtomicInteger
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter

// https://github.com/open-telemetry/opentelemetry-dotnet/blob/main/src/OpenTelemetry.Exporter.Console/ConsoleActivityExporter.cs
class OpenTelemetryTraceConsoleParser {
    private class PendingOpenTelemetryActivity {
        var traceId: String? = null
        var spanId: String? = null
        var activityTraceFlags: ActivityTraceFlags? = null
        var traceStateString: String? = null
        var parentSpanId: String? = null
        var displayName: String? = null
        var kind: ActivityKind? = null
        var startTimeUtc: Instant? = null
        var duration: Duration? = null
        var status: ActivityStatusCode = ActivityStatusCode.Unset
        var statusDescription: String? = null
        var tags: Map<String, String> = emptyMap()
        var events: List<ActivityEvent> = emptyList()
        var links: List<ActivityLink> = emptyList()
        var source: ActivitySource? = null
        var resource: Resource? = null
    }


    fun parseTrace(lines: List<String>): OpenTelemetryTrace? {
        val lineIndex = AtomicInteger(0)

        val activity = PendingOpenTelemetryActivity()
        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            val keyIdentifier = line.substringBefore(":")
            val value = line.substringAfter(":").trim()
            when (keyIdentifier) {
                "Activity.TraceId" -> activity.traceId = value
                "Activity.SpanId" -> activity.spanId = value
                "Activity.TraceFlags" -> activity.activityTraceFlags = if (value == "Recorded") ActivityTraceFlags.Recorded else ActivityTraceFlags.None
                "Activity.TraceState" -> activity.traceStateString = value
                "Activity.ParentSpanId" -> activity.parentSpanId = value
                "Activity.DisplayName" -> activity.displayName = value
                "Activity.Kind" -> activity.kind = parseActivityKind(value)
                "Activity.StartTime" -> activity.startTimeUtc = parseTimestamp(value)
                "Activity.Duration" -> activity.duration = TimeSpan.parse(value).toDuration()
                "Activity.Tags" -> activity.tags = parseKeyValues("    ", lines, lineIndex)
                "StatusCode" -> activity.status = parseStatusCode(value)
                "Activity.StatusDescription" -> activity.statusDescription = value
                "Activity.Events" -> activity.events = parseEvents(lines, lineIndex)
                "Activity.Links" -> activity.links = parseLinks(lines, lineIndex)
                "Instrumentation scope (ActivitySource)" -> activity.source = parseSource(lines, lineIndex)
                "Resource associated with Activity" -> activity.resource =
                    Resource(parseKeyValues("    ", lines, lineIndex))
            }

            lineIndex.incrementAndGet()
        }

        return OpenTelemetryTrace(
            activity.traceId ?: return null,
            activity.spanId ?: return null,
            activity.activityTraceFlags ?: return null,
            activity.traceStateString,
            activity.parentSpanId,
            activity.displayName ?: return null,
            activity.kind ?: return null,
            activity.startTimeUtc ?: return null,
            activity.duration ?: return null,
            activity.status,
            activity.statusDescription,
            activity.tags,
            activity.events,
            activity.links,
            activity.source ?: return null,
            activity.resource,
        )
    }

    private fun parseSource(lines: List<String>, lineIndex: AtomicInteger): ActivitySource? {
        lineIndex.incrementAndGet()
        var name: String? = null
        var version: String? = null
        var tags: Map<String, String>? = null
        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            if (!line.startsWith("    ")) {
                lineIndex.decrementAndGet()
                break
            }
            val keyIdentifier = line.substringBefore(":").trim()
            val value = line.substringAfter(":").trim()
            when (keyIdentifier) {
                "Name" -> name = value
                "Version" -> version = value
                "Tags" -> tags = parseKeyValues("        ", lines, lineIndex)
            }

            lineIndex.incrementAndGet()
        }

        if (name != null)
            return ActivitySource(name, version, tags)
        return null
    }

    private fun parseTimestamp(value: String): Instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value))

    private fun parseEvents(lines: List<String>, lineIndex: AtomicInteger): List<ActivityEvent> {
        val events = mutableListOf<ActivityEvent>()
        lineIndex.incrementAndGet()

        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            if (!line.startsWith("    ")) {
                lineIndex.decrementAndGet()
                break
            }

            val name = line.substringBefore('[').trim()
            val timestamp = parseTimestamp(line.substringAfter('[').substringBefore(']').trim())
            val tags = parseKeyValues("        ", lines, lineIndex)
            events.add(ActivityEvent(name, timestamp, tags))

            lineIndex.incrementAndGet()
        }
        return events
    }

    private fun parseLinks(lines: List<String>, lineIndex: AtomicInteger): List<ActivityLink> {
        val links = mutableListOf<ActivityLink>()
        lineIndex.incrementAndGet()

        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            if (!line.startsWith("    ")) {
                lineIndex.decrementAndGet()
                break
            }

            val traceId = line.substringBeforeLast(' ').trim()
            val spanId = line.substringAfterLast(' ').trim()
            val tags = parseKeyValues("        ", lines, lineIndex)
            links.add(ActivityLink(SpanContext(traceId, spanId), tags))

            lineIndex.incrementAndGet()
        }
        return links
    }

    private fun parseKeyValues(indent: String, lines: List<String>, lineIndex: AtomicInteger): Map<String, String> {
        val tags = mutableMapOf<String, String>()
        lineIndex.incrementAndGet()

        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.get()]
            if (!line.startsWith(indent)) {
                lineIndex.decrementAndGet()
                break
            }

            val key = line.substringBefore(":").trim()
            val value = line.substringAfter(":").trim()
            tags[key] = value

            lineIndex.incrementAndGet()
        }

        return tags
    }

    private fun parseStatusCode(status: String): ActivityStatusCode {
        return when (status.uppercase()) {
            "OK" -> ActivityStatusCode.Ok
            "ERROR" -> ActivityStatusCode.Error
            else -> ActivityStatusCode.Unset
        }
    }

    private fun parseActivityKind(kind: String): ActivityKind {
        return when (kind.uppercase()) {
            "INTERNAL" -> ActivityKind.Internal
            "SERVER" -> ActivityKind.Server
            "CLIENT" -> ActivityKind.Client
            "PRODUCER" -> ActivityKind.Producer
            "CONSUMER" -> ActivityKind.Consumer
            else -> ActivityKind.Internal
        }
    }
}

