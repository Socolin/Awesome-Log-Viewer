package fr.socolin.awesomeLogViewer.module.applicationInsights.network

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class ApplicationInsightsProfileHttpHandler() : HttpHandler {
    override fun handle(exchange: HttpExchange?) {
        if (exchange == null) return

        val response = "1234".toByteArray()
        exchange.sendResponseHeaders(200, response.size.toLong())
        exchange.responseBody.write(response)
        exchange.responseBody.close()
    }
}
