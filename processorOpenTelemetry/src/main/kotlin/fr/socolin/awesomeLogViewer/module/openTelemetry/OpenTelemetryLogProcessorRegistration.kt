package fr.socolin.awesomeLogViewer.module.openTelemetry

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.module.openTelemetry.console.OpenTelemetryConsoleLogProcessor
import fr.socolin.awesomeLogViewer.module.openTelemetry.network.OpenTelemetryNetworkLogProcessor

class OpenTelemetryLogProcessorRegistration() : ProjectActivity {
    override suspend fun execute(project: Project) {
        LogProcessorManager.getInstance(project).registerLogProcessor(OpenTelemetryConsoleLogProcessor.Definition())
        LogProcessorManager.getInstance(project).registerLogProcessor(OpenTelemetryNetworkLogProcessor.Definition())
    }
}
