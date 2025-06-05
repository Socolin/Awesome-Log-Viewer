package fr.socolin.awesomeLogViewer.module.applicationInsights.network

import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import com.jetbrains.rd.util.reactive.Signal
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class ApplicationInsightsProfileHttpHandler(private val logReceived: Signal<LogEntry>) : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        if (exchange == null) return

        val response = "1234".toByteArray()
        exchange.sendResponseHeaders(200, response.size.toLong())
        exchange.responseBody.write(response)
        exchange.responseBody.close()
    }
}
