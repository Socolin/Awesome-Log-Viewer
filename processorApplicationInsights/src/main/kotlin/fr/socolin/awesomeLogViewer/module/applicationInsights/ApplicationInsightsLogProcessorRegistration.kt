package fr.socolin.awesomeLogViewer.module.applicationInsights

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.module.applicationInsights.console.ApplicationInsightsConsoleLogProcessor
import fr.socolin.awesomeLogViewer.module.applicationInsights.network.ApplicationInsightsNetworkLogProcessor

class ApplicationInsightsLogProcessorRegistration() : ProjectActivity {
    override suspend fun execute(project: Project) {
        LogProcessorManager.getInstance(project).registerLogProcessor(ApplicationInsightsConsoleLogProcessor.Definition())
        LogProcessorManager.getInstance(project).registerLogProcessor(ApplicationInsightsNetworkLogProcessor.Definition())
    }
}
