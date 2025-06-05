package fr.socolin.awesomeLogViewer.module.applicationInsights.network

import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader.getIcon
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorDefinition
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.FilterSectionDefinition
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsNetworkSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsSettingsStorageService
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import javax.swing.Icon

private val LOG = logger<ApplicationInsightsNetworkLogProcessor>()

class ApplicationInsightsNetworkLogProcessor(
    definition: LogProcessorDefinition,
    private val applicationInsightsSettings: ApplicationInsightsNetworkSettingsState
) : NetworkLogProcessor(definition, applicationInsightsSettings) {
    private var server: HttpServer? = null

    override fun getFilterSectionsDefinitions(): List<FilterSectionDefinition> {
        return listOf(
            FilterSectionDefinition("AI_SeverityLevel", "Severity Level"),
            FilterSectionDefinition("AI_LogType", "Log Type"),
        )
    }

    override fun getRawLogLanguage(): Language? {
        return JsonLanguage.INSTANCE
    }

    override fun startNetworkCollector() {
        cleanPendingTelemetries()
        try {
            if (this.server == null) {
                val server = HttpServer.create(InetSocketAddress(applicationInsightsSettings.listenPortNumber.value), 0)
                server.executor = Executors.newFixedThreadPool(1)
                server.createContext("/v2/track", ApplicationInsightsHttpHandler(notifyLogReceived))
                server.createContext("/v2.1/track", ApplicationInsightsHttpHandler(notifyLogReceived))
                server.createContext(
                    "/api/profiles/12345678-0000-0000-0000-009876543210/appId",
                    ApplicationInsightsProfileHttpHandler(notifyLogReceived)
                )
                server.start()

                this.server = server
            }
        } catch (e: Exception) {
            LOG.error("Failed to start Application Insights Network Collector", e)
        }
        return
    }

    override fun getEnvironmentVariables(): Map<String, String> {
        return applicationInsightsSettings.environmentVariables.mapValues {
            it.value.replace("\${SERVER_PORT}", server?.address?.port.toString())
        }
    }

    private fun cleanPendingTelemetries() {
        val tmpFolders = mutableListOf<String>()
        if (System.getProperty("os.name").lowercase().contains("win")) {
            System.getenv("LOCALAPPDATA")?.let { tmpFolders.add(it) }
            System.getenv("TEMP")?.let { tmpFolders.add(it) }
        } else {
            System.getenv("TMPDIR")?.let { tmpFolders.add(it) }
            tmpFolders.add("/var/tmp")
            tmpFolders.add("/tmp")
        }

        val pathSuffix = "Microsoft/ApplicationInsights"

        LOG.info("Deleting stale Application Insights Stale Telemetries")
        for (tmpFolder in tmpFolders) {
            val aiPath = File(tmpFolder, pathSuffix)
            if (aiPath.exists() && aiPath.isDirectory) {
                aiPath.listFiles()?.forEach { folder ->
                    LOG.info("Deleting ${folder.absolutePath}")
                    if (folder.isDirectory) {
                        folder.listFiles()?.forEach { file ->
                            if (file.isFile) {
                                try {
                                    file.delete()
                                } catch (_: Exception) {
                                    // Ignore deletion failures
                                }
                            }
                        }
                        try {
                            folder.delete()
                        } catch (_: Exception) {
                            // Ignore deletion failures
                        }
                    }
                }
            }
        }
    }

    override fun dispose() {
        server?.stop(0)
        server = null
    }

    class Definition : LogProcessorDefinition() {
        override fun getId(): String {
            return "applicationInsightsNetwork"
        }

        override fun getDisplayName(): String {
            return "Application Insights"
        }

        override fun getIcon(): Icon {
            return Icon
        }

        override fun createProcessorIfActive(
            project: Project,
            executionMode: ExecutionMode,
        ): LogProcessor? {
            val settings = ApplicationInsightsSettingsStorageService.Companion.getInstance(project)
            if (!shouldCreateProcessor(settings.state.networkSettings, executionMode)) {
                return null;
            }
            return ApplicationInsightsNetworkLogProcessor(this, settings.state.networkSettings)
        }
    }

    companion object {
        val Icon: Icon = getIcon("/icons/application-insights.svg", ApplicationInsightsNetworkLogProcessor::class.java)
    }
}

