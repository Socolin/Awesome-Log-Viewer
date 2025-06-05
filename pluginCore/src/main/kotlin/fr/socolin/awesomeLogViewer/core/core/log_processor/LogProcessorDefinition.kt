package fr.socolin.awesomeLogViewer.core.core.log_processor

import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.LogProcessorSettingsState
import javax.swing.Icon

abstract class LogProcessorDefinition {
    abstract fun getId(): String
    abstract fun getDisplayName(): String
    abstract fun getIcon(): Icon
    abstract fun createProcessorIfActive(project: Project, executionMode: ExecutionMode): LogProcessor?

    protected fun shouldCreateProcessor(settings: LogProcessorSettingsState, executionMode: ExecutionMode): Boolean {
        if (executionMode == ExecutionMode.RUN && settings.enableForRun.value) {
            return true
        }
        if (executionMode == ExecutionMode.DEBUG && settings.enableForDebug.value) {
            return true
        }
        return false
    }
}
