package fr.socolin.awesomeLogViewer.module.applicationInsights.network

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.Signal
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogEntryFactory
import kotlinx.coroutines.launch
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.zip.GZIPInputStream

private val LOG = logger<ApplicationInsightsHttpHandler>()

class ApplicationInsightsHttpHandler(
    private val logReceived: Signal<LogEntry>,
    private val overriddenIngestionEndpoint: URI?
) : HttpHandler, Disposable {
    private val httpClient = HttpClient.newHttpClient()

    override fun handle(exchange: HttpExchange?) {
        try {
            if (exchange == null) return

            val rawBytes = exchange.requestBody.readBytes();
            val requestBytes = GZIPInputStream(rawBytes.inputStream()).use { it.readBytes() }
            val jsonBody = String(requestBytes, Charsets.UTF_8).trim()
            val logEntry = ApplicationInsightsLogEntryFactory.Companion.createFromJson(jsonBody, false, true)
            if (logEntry != null) {
                logReceived.fire(logEntry)
            }
            val response = "OK".toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.write(response)
            exchange.responseBody.close()

            forwardLog(rawBytes, exchange)
        } catch (e: Exception) {
            LOG.warn("Failed to process logs from Application Insights", e)
        }
    }

    private fun forwardLog(
        rawBytes: ByteArray,
        exchange: HttpExchange
    ) {
        if (overriddenIngestionEndpoint == null) return

        try {
            val httpRequestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofByteArray(rawBytes))
                .uri(
                    URI(
                        overriddenIngestionEndpoint.scheme,
                        overriddenIngestionEndpoint.host,
                        exchange.requestURI.path,
                        exchange.requestURI.query,
                        null
                    )
                )

            exchange.requestHeaders.forEach {
                if (it.key.lowercase() == "content-length")
                    return@forEach
                if (it.key.lowercase() == "host")
                    return@forEach
                httpRequestBuilder.header(it.key, it.value.first())
            }

            val httpRequest = httpRequestBuilder.build()

            // Fire and forget - handle the response to avoid leaks
            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray())
                .handle { response, throwable ->
                    // Consume the response to avoid resource leaks
                    // No need to process the result since it's fire-and-forget
                    if (throwable != null) {
                        LOG.warn("Failed to forward Application Insights telemetry", throwable)
                    } else if (response.statusCode() !in 200..299) {
                        LOG.warn("Failed to forward Application Insights telemetry: HTTP ${response.statusCode()} - ${String(response.body())}")
                    }
                    null
                }

        } catch (e: Exception) {
            LOG.warn("Failed to forward Application Insights telemetry to overridden ingestion endpoint", e)
        }
    }

    override fun dispose() {
        httpClient.close()
    }
}
