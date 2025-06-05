package fr.socolin.awesomeLogViewer.module.openTelemetry.signals

import java.time.Instant

data class OpenTelemetryMetric(
    val name: String,
    val description: String = "",
    val unit: String = "",
    val metricPoints: List<MetricPoint>,
    val resource: Map<String, String>?
) : OpenTelemetrySignal

enum class MetricType(val value: Int, val displayName: String) {
    // Sum types
    LONG_SUM(0x1a, "LongSum"),
    DOUBLE_SUM(0x1d, "DoubleSum"),

    // Gauge types
    LONG_GAUGE(0x2a, "LongGauge"),
    DOUBLE_GAUGE(0x2d, "DoubleGauge"),

    // Histogram types
    HISTOGRAM(0x40, "Histogram"),
    EXPONENTIAL_HISTOGRAM(0x50, "ExponentialHistogram"),

    // Non-monotonic Sum types
    LONG_SUM_NON_MONOTONIC(0x8a, "LongSumNonMonotonic"),
    DOUBLE_SUM_NON_MONOTONIC(0x8d, "DoubleSumNonMonotonic");

    companion object {
        fun fromDisplayName(displayName: String): MetricType? =
            entries.find { it.displayName.equals(displayName, ignoreCase = true) }
    }
}

data class MetricPoint(
    val startTime: Instant,
    val endTime: Instant,
    val metricType: MetricType,
    val tags: Map<String, String>,
    val longValue: Long? = null,
    val doubleValue: Double? = null,
    val histogramData: HistogramData? = null,
    val exemplars: List<Exemplar>? = null,
    val meterName: String? = null,
    val meterVersion: String? = null,
    val meterTags: Map<String, String>? = null
)

data class HistogramData(
    val sum: Double,
    val count: Long,
    val min: Double? = null,
    val max: Double? = null,
    val buckets: List<HistogramBucket>? = null,
    val exponentialData: ExponentialHistogramData? = null
)

data class HistogramBucket(
    val explicitBound: Double,
    val bucketCount: Long
)

data class ExponentialHistogramData(
    val scale: Int,
    val zeroCount: Long,
    val positiveBuckets: List<ExponentialBucket>
)

data class ExponentialBucket(
    val offset: Int,
    val count: Long
)

data class Exemplar(
    val timestamp: Instant,
    val doubleValue: Double? = null,
    val longValue: Long? = null,
    val traceId: String? = null,
    val spanId: String? = null,
    val filteredTags: Map<String, String>? = null
)
