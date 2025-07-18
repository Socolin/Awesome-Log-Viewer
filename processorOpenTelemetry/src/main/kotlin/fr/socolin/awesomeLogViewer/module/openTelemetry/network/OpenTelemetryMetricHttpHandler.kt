package fr.socolin.awesomeLogViewer.module.openTelemetry.network

import com.jetbrains.rd.util.reactive.Signal
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.OpenTelemetryLogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.*
import io.grpc.stub.StreamObserver
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc
import io.opentelemetry.proto.metrics.v1.Metric
import java.net.URI
import java.time.Instant

class OpenTelemetryMetricHttpHandler(
    private val logReceived: Signal<LogEntry>,
    overriddenIngestionEndpoint: URI?,
) : BaseOpenTelemetryHttpHandler(overriddenIngestionEndpoint) {

    val forwardStub: MetricsServiceGrpc.MetricsServiceStub? = if (forwardChannel != null)
        MetricsServiceGrpc.newStub(forwardChannel)
    else
        null

    val forwardResponseObserver = object : StreamObserver<ExportMetricsServiceResponse> {
        override fun onNext(value: ExportMetricsServiceResponse?) {
        }

        override fun onError(t: Throwable?) {
        }

        override fun onCompleted() {
        }
    }

    override fun processBytes(bytes: ByteArray) {
        val metricRequest = ExportMetricsServiceRequest.parseFrom(bytes)
        forwardStub?.export(metricRequest, forwardResponseObserver)
        val resource = metricRequest.getResourceMetrics(0).resource.attributesList.toMap()
        for (resourceMetric in metricRequest.resourceMetricsList) {
            for (scopedMetric in resourceMetric.scopeMetricsList) {
                for (metric in scopedMetric.metricsList) {
                    val metricPoints = createMetricPoints(metric)
                    val metricSignal = OpenTelemetryMetric(
                        metric.name,
                        metric.description,
                        metric.unit,
                        metricPoints,
                        resource.entries.associate { Pair(it.key, it.value) },
                    )
                    if (metricPoints.isEmpty())
                        continue

                    logReceived.fire(OpenTelemetryLogEntry.Companion.createFromSignal(metric.toString(), metricSignal))
                }
            }
        }
    }

    private fun createMetricPoints(metric: Metric): List<MetricPoint> {
        val metricPoints = mutableListOf<MetricPoint>()

        if (metric.hasSum()) {
            for (dataPoint in metric.sum.dataPointsList) {
                if (dataPoint.hasAsInt()) {
                    metricPoints.add(
                        MetricPoint(
                            Instant.ofEpochMilli(dataPoint.startTimeUnixNano / 1_000_000),
                            Instant.ofEpochMilli(dataPoint.timeUnixNano / 1_000_000),
                            MetricType.LONG_SUM,
                            dataPoint.attributesList.toMap(),
                            dataPoint.asInt,
                        )
                    )
                } else if (dataPoint.hasAsInt()) {
                    metricPoints.add(
                        MetricPoint(
                            Instant.ofEpochMilli(dataPoint.startTimeUnixNano / 1_000_000),
                            Instant.ofEpochMilli(dataPoint.timeUnixNano / 1_000_000),
                            MetricType.DOUBLE_SUM,
                            dataPoint.attributesList.toMap(),
                            doubleValue = dataPoint.asDouble,
                        )
                    )
                }
            }
        }

        if (metric.hasGauge()) {
            for (dataPoint in metric.gauge.dataPointsList) {
                if (dataPoint.hasAsInt()) {
                    metricPoints.add(
                        MetricPoint(
                            Instant.ofEpochMilli(dataPoint.startTimeUnixNano / 1_000_000),
                            Instant.ofEpochMilli(dataPoint.timeUnixNano / 1_000_000),
                            MetricType.LONG_GAUGE,
                            dataPoint.attributesList.toMap(),
                            dataPoint.asInt,
                        )
                    )
                }
                if (dataPoint.hasAsDouble()) {
                    metricPoints.add(
                        MetricPoint(
                            Instant.ofEpochMilli(dataPoint.startTimeUnixNano / 1_000_000),
                            Instant.ofEpochMilli(dataPoint.timeUnixNano / 1_000_000),
                            MetricType.DOUBLE_GAUGE,
                            dataPoint.attributesList.toMap(),
                            doubleValue = dataPoint.asDouble,
                        )
                    )
                }
            }
        }

        if (metric.hasHistogram() || metric.hasExponentialHistogram()) {
            for (dataPoint in metric.histogram.dataPointsList) {
                val buckets = mutableListOf<HistogramBucket>()
                for (i in 0 until dataPoint.bucketCountsCount) {
                    if (i >= dataPoint.explicitBoundsList.size)
                        break
                    if (i >= dataPoint.bucketCountsList.size)
                        break
                    buckets.add(HistogramBucket(dataPoint.explicitBoundsList[i], dataPoint.bucketCountsList[i]))
                }
                metricPoints.add(
                    MetricPoint(
                        Instant.ofEpochMilli(dataPoint.startTimeUnixNano / 1_000_000),
                        Instant.ofEpochMilli(dataPoint.timeUnixNano / 1_000_000),
                        if (metric.hasHistogram()) MetricType.HISTOGRAM else MetricType.EXPONENTIAL_HISTOGRAM,
                        dataPoint.attributesList.toMap(),
                        histogramData = HistogramData(
                            dataPoint.sum,
                            dataPoint.count,
                            dataPoint.min,
                            dataPoint.max,
                            buckets
                        )
                    )
                )
            }
        }

        if (metric.hasSummary()) {
            // FIXME
        }

        return metricPoints
    }
}
