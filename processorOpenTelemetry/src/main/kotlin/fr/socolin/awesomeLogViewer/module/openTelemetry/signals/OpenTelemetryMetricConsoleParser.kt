package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

import com.jetbrains.rd.util.AtomicInteger
import java.time.Instant
import java.time.format.DateTimeFormatter

// https://github.com/open-telemetry/opentelemetry-dotnet/blob/main/src/OpenTelemetry.Exporter.Console/ConsoleMetricExporter.cs
class OpenTelemetryMetricConsoleParser {
    class PendingOpenTelemetryMetric {
        var name: String? = null
        var description: String = ""
        var unit: String = ""
        var metricPoints: MutableList<MetricPoint> = mutableListOf()
    }

    fun parseMetric(lines: List<String>): OpenTelemetryMetric? {
        val lineIndex = AtomicInteger(0)

        val metric = PendingOpenTelemetryMetric()
        parseMetricName(metric, lines[lineIndex.getAndIncrement()])

        while (lineIndex.get() < lines.size) {
            val metricPoint = parseMetricPoint(lines, lineIndex) ?: break
            metric.metricPoints.add(metricPoint)
        }

        return OpenTelemetryMetric(
            metric.name ?: return null,
            metric.description,
            metric.unit,
            metric.metricPoints,
            mapOf()
        )
    }

    val timeRangePattern =
        """\((\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+Z), (\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+Z)]""".toRegex()

    private fun parseMetricPoint(lines: List<String>, lineIndex: AtomicInteger): MetricPoint? {
        var startTime: Instant? = null
        var endTime: Instant? = null
        var metricType: MetricType? = null
        var tags: MutableMap<String, String>? = null
        while (lineIndex.get() < lines.size) {
            val line = lines[lineIndex.getAndIncrement()]
            val timeRangeMatchResult = timeRangePattern.find(line)
            if (timeRangeMatchResult != null) {
                val subLines = line.split("\n")
                val metricPointDefinitionLine = subLines.first()
                val (startTimestamp, endTimestamp) = timeRangeMatchResult.destructured
                startTime = parseTimestamp(startTimestamp)
                endTime = parseTimestamp(endTimestamp)
                metricType = MetricType.fromDisplayName(metricPointDefinitionLine.substringAfterLast(" "))
                val tagString = metricPointDefinitionLine.substringAfter("]").substringBeforeLast(" ")
                tags = parseTagsString(tagString)
            }
        }

        return MetricPoint(
            startTime ?: return null,
            endTime ?: return null,
            metricType ?: return null,
            tags ?: return null,
        )
    }

    private fun parseTagsString(tagString: String): MutableMap<String, String> {
        val tags: MutableMap<String, String> = mutableMapOf()

        val split = tagString.split(Regex("\\w: "))
        var key = split.firstOrNull()
        if (key != null) {
            var value: String?
            for (s in split.slice(1 until split.size - 2)) {
                value = s.substringBeforeLast(' ')
                tags[key!!] = value.trim()
                key = s.substringAfterLast(' ')
            }
            value = split.lastOrNull()
            tags[key] = value!!.trim()
        }

        return tags
    }

    private fun parseTimestamp(value: String): Instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value))

    private val metricPattern =
        """Metric Name: (.*?)(?:, Description: (.*?))?(?:, Unit: (.*?))?$""".toRegex(RegexOption.MULTILINE)

    private fun parseMetricName(metric: PendingOpenTelemetryMetric, line: String) {
        val matchResult = metricPattern.find(line)

        if (matchResult != null) {
            metric.name = matchResult.groupValues[1]
            metric.description = matchResult.groupValues[2]
            metric.description = matchResult.groupValues[3]
        }
    }
}
