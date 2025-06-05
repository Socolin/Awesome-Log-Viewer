package fr.socolin.awesomeLogViewer.core.core.tool_window.toolbar

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class ToggleToolbarAction(
    icon: Icon,
    @Nls(capitalization = Nls.Capitalization.Title) message: String,
    @Nls(capitalization = Nls.Capitalization.Sentence) description: String,
    private var state: Boolean,
    private val onSelect: (Boolean) -> Unit
) :
    ToggleAction() {
    init {
        this.templatePresentation.description = description
        this.templatePresentation.text = message
        this.templatePresentation.icon = icon
    }

    override fun isSelected(actionEvent: AnActionEvent): Boolean {
        return state
    }

    override fun setSelected(actionEvent: AnActionEvent, state: Boolean) {
        this.state = state
        onSelect.invoke(state)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
