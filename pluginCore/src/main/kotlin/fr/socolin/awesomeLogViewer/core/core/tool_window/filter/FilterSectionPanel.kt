package fr.socolin.awesomeLogViewer.core.core.tool_window.filter

import com.intellij.find.FindBundle
import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil.getMnemonicAsShortcut
import com.intellij.openapi.actionSystem.ex.TooltipDescriptionProvider
import com.intellij.openapi.actionSystem.ex.TooltipLinkProvider
import com.intellij.openapi.actionSystem.ex.TooltipLinkProvider.TooltipLink
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.actionSystem.impl.FieldInplaceActionButtonLook
import com.intellij.openapi.project.DumbAwareToggleAction
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBInsets
import fr.socolin.awesomeLogViewer.core.core.session.FilterSection
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import com.jetbrains.rd.swing.textProperty
import com.jetbrains.rd.util.reactive.Property
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.Icon
import javax.swing.JComponent

class FilterSectionPanel(
    private val logSession: LogSession
) : JBPanel<FilterSectionPanel>() {
    init {
        layout = GridBagLayout()

        val pluginSettings = GlobalPluginSettingsStorageService.Companion.getInstance()
        val searchTextField = SearchTextField(false)
        val toggleCaseSensitiveAction = MySwitchStateToggleAction(
            "find.popup.case.sensitive",
            AllIcons.Actions.MatchCase, AllIcons.Actions.MatchCaseHovered, AllIcons.Actions.MatchCaseSelected,
            pluginSettings.state.isFilterCaseSensitive,
            searchTextField.textEditor
        )
        val caseSensitiveButton = MySimpleActionButton(toggleCaseSensitiveAction, true)
        pluginSettings.state.isFilterCaseSensitive.advise(logSession.lifetime) {
            Toggleable.setSelected(caseSensitiveButton.presentation, it)
        }
        searchTextField.textEditor.textProperty().advise(logSession.lifetime) {
            logSession.sessionFilter.updateTextFilterValue(it)
        }
        logSession.sessionFilter.filterChanged.advise(logSession.lifetime) {
            if (it.filterText != searchTextField.textEditor.text) {
                searchTextField.text = it.filterText
            }
        }
        add(searchTextField, GridBagConstraints().apply {
            gridwidth = 100
            gridy = 1
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        })
        add(caseSensitiveButton, GridBagConstraints().apply {
            gridx = 100
            gridy = 1
            insets = JBInsets.create(8, 8)
        })

        for (filterSection in logSession.sessionFilter.filterSections.values) {
            addSection(filterSection)
        }
        logSession.sessionFilter.sectionAdded.advise(logSession.lifetime) {
            addSection(it)
        }
    }

    private fun addSection(filterSection: FilterSection) {
        val initialFilteredValues = logSession.sessionFilter.filteredValues[filterSection.sectionName]
        val filterComponent = FilterComponent(filterSection.displayName, filterSection.filterValues, initialFilteredValues)
        filterSection.valueAdded.advise(logSession.lifetime) {
            filterComponent.addValue(it, logSession.sessionFilter.filteredValues[filterSection.sectionName]?.contains(it.definition.value) ?: false)
        }
        filterComponent.onFilterChanged.advise(logSession.lifetime) {
            logSession.sessionFilter.setFilteredValue(filterSection.sectionName, it.value, it.filtered)
        }
        add(filterComponent, GridBagConstraints().apply {
            gridwidth = 1
            gridy = 2
            gridheight = 1
            fill = GridBagConstraints.NONE
            weightx = 0.0
            anchor = GridBagConstraints.LINE_START
        })
    }

    class MySimpleActionButton internal constructor(action: AnAction, focusable: Boolean) :
        ActionButton(
            action,
            action.templatePresentation.clone(),
            "AwesomeLogViewerFilterTextField",
            ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE
        ) {
        init {
            setLook(FieldInplaceActionButtonLook())
            isFocusable = focusable
            updateIcon()
        }

        override fun getDataContext(): DataContext {
            return DataManager.getInstance().getDataContext(this)
        }

        override fun getPopState(): Int {
            return if (isSelected) SELECTED else super.getPopState()
        }

        override fun getIcon(): Icon {
            if (isEnabled && isSelected) {
                val selectedIcon = myPresentation.selectedIcon
                if (selectedIcon != null) return selectedIcon
            }
            return super.getIcon()
        }
    }

    private class MySwitchStateToggleAction(
        message: String,
        icon: Icon, hoveredIcon: Icon, selectedIcon: Icon,
        private val property: Property<Boolean>,
        component: JComponent,
        private val myTooltipLink: TooltipLink? = null
    ) : DumbAwareToggleAction(FindBundle.message(message), null, icon),
        TooltipLinkProvider,
        TooltipDescriptionProvider {
        init {
            templatePresentation.hoveredIcon = hoveredIcon
            templatePresentation.selectedIcon = selectedIcon
            val shortcut = getMnemonicAsShortcut(this)
            if (shortcut != null) {
                registerCustomShortcutSet(shortcut, component)
            }
        }

        override fun getTooltipLink(owner: JComponent?): TooltipLink? {
            return myTooltipLink
        }

        override fun isSelected(e: AnActionEvent): Boolean {
            return property.value
        }

        override fun update(e: AnActionEvent) {
            Toggleable.setSelected(e.presentation, property.value)
        }

        override fun getActionUpdateThread(): ActionUpdateThread {
            return ActionUpdateThread.EDT
        }

        override fun setSelected(e: AnActionEvent, selected: Boolean) {
            property.set(selected)
        }
    }
}
