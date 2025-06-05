package fr.socolin.awesomeLogViewer.core.core.tool_window.filter

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class SimpleAction(
    private val label: String,
    private val onClick: () -> Unit
) : AnAction(), DumbAware {
    init {
        this.templatePresentation.description = label
        this.templatePresentation.text = label
    }

    override fun actionPerformed(p0: AnActionEvent) {
        onClick.invoke()
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
