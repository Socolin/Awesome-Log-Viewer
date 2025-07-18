package fr.socolin.awesomeLogViewer.module.openTelemetry.network

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorDefinition
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetryNetworkSettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetrySettingsStorageService
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.opentelemetry.proto.common.v1.KeyValue
import java.net.InetSocketAddress
import java.net.URI
import java.util.concurrent.Executors
import javax.swing.Icon

private val LOG = logger<OpenTelemetryNetworkLogProcessor>()

class OpenTelemetryNetworkLogProcessor(
    definition: LogProcessorDefinition,
    private val openTelemetrySettings: OpenTelemetryNetworkSettingsState
) : NetworkLogProcessor(definition, openTelemetrySettings) {
    private var server: HttpServer? = null
    private var overriddenCollectorEndpoint: URI? = null
    private var traceHttpHandler: OpenTelemetryTraceHttpHandler? = null
    private var metricHttpHandler: OpenTelemetryMetricHttpHandler? = null
    private var logHttpHandler: OpenTelemetryLogHttpHandler? = null

    override fun getFilterSectionsDefinitions(): List<FilterSectionDefinition> {
        return listOf(
            FilterSectionDefinition("OT_SignalType", "Log Type"),
            FilterSectionDefinition("OT_Severity", "Severity Level"),
        )
    }

    override fun getRawLogLanguage(): Language? {
        return PlainTextLanguage.INSTANCE
    }

    override fun dispose() {
        traceHttpHandler?.dispose()
        traceHttpHandler = null
        metricHttpHandler?.dispose()
        metricHttpHandler = null
        logHttpHandler?.dispose()
        logHttpHandler = null
        server?.stop(0)
        server = null
    }

    override fun startNetworkCollector(environment: Map<String, String>) {
        try {
            if (this.server == null) {
                setupLogForwarding(environment)

                val server = HttpServer.create(InetSocketAddress(openTelemetrySettings.listenPortNumber.value), 0)
                server.executor = Executors.newFixedThreadPool(1)
                traceHttpHandler = OpenTelemetryTraceHttpHandler(notifyLogReceived, overriddenCollectorEndpoint)
                metricHttpHandler = OpenTelemetryMetricHttpHandler(notifyLogReceived, overriddenCollectorEndpoint)
                logHttpHandler = OpenTelemetryLogHttpHandler(notifyLogReceived, overriddenCollectorEndpoint)
                server.createContext("/v1/traces", traceHttpHandler)
                server.createContext("/v1/metrics", metricHttpHandler)
                server.createContext("/v1/logs", logHttpHandler)
                server.start()

                this.server = server
            }
        } catch (e: Exception) {
            LOG.error("Failed to start Open Telemetry server", e)
        }
    }

    private fun setupLogForwarding(environment: Map<String, String>) {
        if (!openTelemetrySettings.forwardLogs.value) {
            return;
        }

        val collectorEndpoint = environment["OTEL_EXPORTER_OTLP_ENDPOINT"]
        if (collectorEndpoint == null) {
            return
        }
        overriddenCollectorEndpoint = URI(collectorEndpoint)
    }

    override fun getEnvironmentVariables(): Map<String, String> {
        return openTelemetrySettings.environmentVariables.mapValues {
            it.value.replace("\${SERVER_PORT}", server?.address?.port.toString())
        }
    }

    class Definition : LogProcessorDefinition() {
        override fun getId(): String {
            return "openTelemetryNetwork"
        }

        override fun getDisplayName(): String {
            return "Open Telemetry"
        }

        override fun getIcon(): Icon {
            return Icon
        }

        override fun createProcessorIfActive(project: Project, executionMode: ExecutionMode): LogProcessor? {
            val settings = OpenTelemetrySettingsStorageService.Companion.getInstance(project)
            if (!shouldCreateProcessor(settings.state.networkSettings, executionMode)) {
                return null
            }
            return OpenTelemetryNetworkLogProcessor(this, settings.state.networkSettings)
        }
    }

    companion object {
        val Icon: Icon = getIcon("/icons/openTelemetry.svg", OpenTelemetryNetworkLogProcessor::class.java)
    }
}

abstract class BaseOpenTelemetryHttpHandler(
    overriddenIngestionEndpoint: URI?,
) : HttpHandler, Disposable {
    protected val forwardChannel: ManagedChannel? = if (overriddenIngestionEndpoint != null)
        ManagedChannelBuilder.forAddress(overriddenIngestionEndpoint.host, overriddenIngestionEndpoint.port)
            .usePlaintext()
            .build()
    else
        null

    override fun dispose() {
        forwardChannel?.shutdownNow()
    }

    override fun handle(exchange: HttpExchange?) {
        if (exchange == null) return

        try {
            val requestBytes = exchange.requestBody.readBytes()
            processBytes(requestBytes)
        } catch (e: Exception) {
            LOG.warn("Received an unprocessable telemetry on Open Telemetry server", e)
        }

        val response = "OK".toByteArray()
        exchange.sendResponseHeaders(200, response.size.toLong())
        exchange.responseBody.write(response)
        exchange.responseBody.close()
    }

    abstract fun processBytes(bytes: ByteArray)
}

fun List<KeyValue>.toMap(): Map<String, String> {
    return this.associate {
        var value = ""
        if (it.value.hasStringValue())
            value = it.value.stringValue
        else if (it.value.hasBoolValue())
            value = it.value.boolValue.toString()
        else if (it.value.hasIntValue())
            value = it.value.intValue.toString()
        else if (it.value.hasDoubleValue())
            value = it.value.doubleValue.toString()
        Pair(it.key, value)
    }
}

fun com.google.protobuf.ByteString.toHexString(): String? {
    if (isEmpty)
        return null
    return map { it.toUByte() }.joinToString("")
}
