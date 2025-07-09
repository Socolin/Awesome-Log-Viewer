package fr.socolin.awesomeLogViewer.core.core.tool_window.toolbar

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class DisabledAction(
    icon: Icon,
    @Nls(capitalization = Nls.Capitalization.Title) message: String,
    @Nls(capitalization = Nls.Capitalization.Sentence) description: String,
) : AnAction() {
    init {
        this.templatePresentation.description = description
        this.templatePresentation.text = message
        this.templatePresentation.icon = icon
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabled = false
    }

    override fun actionPerformed(p0: AnActionEvent) {
    }
}
