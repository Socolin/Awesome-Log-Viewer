package fr.socolin.awesomeLogViewer.core.core.tool_window.filter

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class FilterSelectorToolbarAction(
    label: String,
    private var selected: Boolean,
    private val onSelectionChanged: (Boolean) -> Unit
) : ToggleAction() {
    init {
        this.templatePresentation.description = label
        this.templatePresentation.text = label
    }

    override fun isSelected(actionEvent: AnActionEvent): Boolean {
        return selected
    }

    override fun setSelected(actionEvent: AnActionEvent, state: Boolean) {
        selected = !selected;
        onSelectionChanged.invoke(selected)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
