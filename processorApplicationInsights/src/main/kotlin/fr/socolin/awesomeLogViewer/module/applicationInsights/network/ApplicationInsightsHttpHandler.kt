package fr.socolin.awesomeLogViewer.module.applicationInsights.network

import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsLogEntryFactory
import com.jetbrains.rd.util.reactive.Signal
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.util.zip.GZIPInputStream

class ApplicationInsightsHttpHandler(private val logReceived: Signal<LogEntry>) : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        if (exchange == null) return

        val requestBytes = GZIPInputStream(exchange.requestBody).use { it.readBytes() }
        val jsonBody = String(requestBytes, Charsets.UTF_8).trim()
        val logEntry = ApplicationInsightsLogEntryFactory.Companion.createFromJson(jsonBody, false, true)
        if (logEntry != null) {
            logReceived.fire(logEntry)
        }
        val response = "OK".toByteArray()
        exchange.sendResponseHeaders(200, response.size.toLong())
        exchange.responseBody.write(response)
        exchange.responseBody.close()
    }
}
