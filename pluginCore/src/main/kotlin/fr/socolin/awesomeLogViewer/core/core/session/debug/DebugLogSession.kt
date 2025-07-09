package fr.socolin.awesomeLogViewer.core.core.session.debug

import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService

class DebugLogSession(
    logProcessor: LogProcessor,
    project: Project,
    private val getUi : () -> RunnerLayoutUi?,
) : LogSession(project, GlobalPluginSettingsStorageService.Companion.getInstance(), logProcessor,) {
    override fun getRunnerLayoutUi(): RunnerLayoutUi? {
        return getUi.invoke()
    }
}
