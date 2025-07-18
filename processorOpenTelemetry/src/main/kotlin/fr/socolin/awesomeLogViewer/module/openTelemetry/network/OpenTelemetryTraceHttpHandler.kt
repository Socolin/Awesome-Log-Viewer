package fr.socolin.awesomeLogViewer.module.openTelemetry.network

import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.Signal
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.OpenTelemetryLogEntry
import fr.socolin.awesomeLogViewer.module.openTelemetry.signals.*
import io.grpc.stub.StreamObserver
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc
import io.opentelemetry.proto.trace.v1.Span
import io.opentelemetry.proto.trace.v1.Status
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class OpenTelemetryTraceHttpHandler(
    private val logReceived: Signal<LogEntry>,
    overriddenIngestionEndpoint: URI?
) : BaseOpenTelemetryHttpHandler(overriddenIngestionEndpoint) {

    val forwardStub: TraceServiceGrpc.TraceServiceStub? = if (forwardChannel != null)
        TraceServiceGrpc.newStub(forwardChannel)
    else
        null

    val forwardResponseObserver = object : StreamObserver<ExportTraceServiceResponse> {
        override fun onNext(value: ExportTraceServiceResponse?) {
        }

        override fun onError(t: Throwable?) {
        }

        override fun onCompleted() {
        }
    }

    override fun processBytes(bytes: ByteArray) {
        val traceRequest = ExportTraceServiceRequest.parseFrom(bytes)
        forwardStub?.export(traceRequest, forwardResponseObserver)
        val resource = traceRequest.getResourceSpans(0).resource
        for (resourceSpans in traceRequest.resourceSpansList) {
            for (scopedSpan in resourceSpans.scopeSpansList) {
                for (span in scopedSpan.spansList) {
                    val trace = OpenTelemetryTrace(
                        span.traceId.toHexString(),
                        span.spanId.toHexString(),
                        ActivityTraceFlags.Recorded,
                        span.traceState,
                        span.parentSpanId.toHexString(),
                        span.name,
                        convertActivityKind(span.kind),
                        Instant.ofEpochMilli(span.startTimeUnixNano / 1_000_000),
                        Duration.of(span.endTimeUnixNano - span.startTimeUnixNano, ChronoUnit.NANOS),
                        convertStatusCode(span.status.code),
                        null,
                        span.attributesList.toMap(),
                        span.eventsList.map {
                            ActivityEvent(
                                it.name,
                                Instant.ofEpochMilli(it.timeUnixNano / 1_000_000),
                                it.attributesList.toMap()
                            )
                        }.toList(),
                        span.linksList.map {
                            ActivityLink(
                                SpanContext(it.traceId.toString(), it.spanId.toString()),
                                it.attributesList.toMap()
                            )
                        }.toList(),
                        null,
                        Resource(resource.attributesList.toMap()),
                    )

                    logReceived.fire(OpenTelemetryLogEntry.Companion.createFromSignal(span.toString(), trace))
                }
            }
        }
    }

    private fun convertStatusCode(code: Status.StatusCode): ActivityStatusCode {
        return when (code) {
            Status.StatusCode.STATUS_CODE_UNSET -> ActivityStatusCode.Unset
            Status.StatusCode.STATUS_CODE_OK -> ActivityStatusCode.Ok
            Status.StatusCode.STATUS_CODE_ERROR -> ActivityStatusCode.Error
            Status.StatusCode.UNRECOGNIZED -> ActivityStatusCode.Unknown
        }
    }

    private fun convertActivityKind(kind: Span.SpanKind): ActivityKind {
        return when (kind) {
            Span.SpanKind.SPAN_KIND_SERVER -> ActivityKind.Server
            Span.SpanKind.SPAN_KIND_UNSPECIFIED -> ActivityKind.Unspecified
            Span.SpanKind.SPAN_KIND_INTERNAL -> ActivityKind.Internal
            Span.SpanKind.SPAN_KIND_CLIENT -> ActivityKind.Client
            Span.SpanKind.SPAN_KIND_PRODUCER -> ActivityKind.Producer
            Span.SpanKind.SPAN_KIND_CONSUMER -> ActivityKind.Consumer
            Span.SpanKind.UNRECOGNIZED -> ActivityKind.Unknown
        }
    }
}
