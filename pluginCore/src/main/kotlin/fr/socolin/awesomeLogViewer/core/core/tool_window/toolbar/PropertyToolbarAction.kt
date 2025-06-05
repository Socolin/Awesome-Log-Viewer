package fr.socolin.awesomeLogViewer.core.core.tool_window.toolbar

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class SimpleToolbarAction(
    icon: Icon,
    @Nls(capitalization = Nls.Capitalization.Title) message: String,
    @Nls(capitalization = Nls.Capitalization.Sentence) description: String,
    private val onAction: (AnActionEvent) -> Unit
) : AnAction(), DumbAware {
    init {
        this.templatePresentation.icon = icon
        this.templatePresentation.description = description
        this.templatePresentation.text = message
    }

    override fun isDumbAware(): Boolean {
        return false
    }

    override fun actionPerformed(event: AnActionEvent) {
        onAction.invoke(event)
    }
}
