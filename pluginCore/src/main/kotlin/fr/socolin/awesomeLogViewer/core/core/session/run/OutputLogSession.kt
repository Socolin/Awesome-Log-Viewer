package fr.socolin.awesomeLogViewer.core.core.session.run

import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunnerLayoutUi
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService


class OutputLogSession(
    logProcessor: LogProcessor,
    private val executionEnvironment: ExecutionEnvironment,
) : LogSession(executionEnvironment.project, GlobalPluginSettingsStorageService.Companion.getInstance(), logProcessor,) {

    override fun getRunnerLayoutUi(): RunnerLayoutUi? {
        return executionEnvironment.contentToReuse?.runnerLayoutUi
    }
}
