package fr.socolin.awesomeLogViewer.module.simpleConsole

import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.util.lifetime
import com.intellij.openapi.startup.ProjectActivity
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleSettingsStorageService

class SimpleConsoleLogProcessorRegistration() : ProjectActivity {
    override suspend fun execute(project: Project) {
        val settings = SimpleConsoleSettingsStorageService.Companion.getInstance(project)
        for (settings in settings.state.consolesSettings) {
            val definition = SimpleConsoleLogProcessor.Definition.fromSettings(settings)
            LogProcessorManager.getInstance(project).registerLogProcessor(definition)
        }
        settings.state.consoleAdded.advise(project.lifetime) {
            val definition = SimpleConsoleLogProcessor.Definition.fromSettings(it)
            LogProcessorManager.getInstance(project).registerLogProcessor(definition)
        }
        settings.state.consoleRemoved.advise(project.lifetime) {
            LogProcessorManager.getInstance(project).unregisterLogProcessor(SimpleConsoleLogProcessor.Definition.buildId(it))
        }
    }
}
