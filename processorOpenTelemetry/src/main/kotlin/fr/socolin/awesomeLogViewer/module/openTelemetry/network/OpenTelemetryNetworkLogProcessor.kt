package fr.socolin.awesomeLogViewer.module.openTelemetry.network

import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorDefinition
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetryNetworkSettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetrySettingsStorageService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.opentelemetry.proto.common.v1.KeyValue
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import javax.swing.Icon

private val LOG = logger<OpenTelemetryNetworkLogProcessor>()

class OpenTelemetryNetworkLogProcessor(
    definition: LogProcessorDefinition,
    private val openTelemetrySettings: OpenTelemetryNetworkSettingsState
) : NetworkLogProcessor(definition, openTelemetrySettings) {
    private var server: HttpServer? = null

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
        server?.stop(0)
        server = null
    }

    override fun startNetworkCollector() {
        try {
            if (this.server == null) {
                val server = HttpServer.create(InetSocketAddress(openTelemetrySettings.listenPortNumber.value), 0)
                server.executor = Executors.newFixedThreadPool(1)
                server.createContext("/v1/traces", OpenTelemetryTraceHttpHandler(notifyLogReceived))
                server.createContext("/v1/metrics", OpenTelemetryMetricHttpHandler(notifyLogReceived))
                server.createContext("/v1/logs", OpenTelemetryLogHttpHandler(notifyLogReceived))
                server.start()

                this.server = server
            }
        } catch (e: Exception) {
            LOG.error("Failed to start Open Telemetry server", e)
        }
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

abstract class BaseOpenTelemetryHttpHandler() : HttpHandler {
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
