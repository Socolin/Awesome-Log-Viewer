package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.rd.createNestedDisposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import fr.socolin.awesomeLogViewer.core.core.tool_window.filter.SimpleAction
import fr.socolin.awesomeLogViewer.module.simpleConsole.SimpleConsoleBundle
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleSettingsState
import com.jetbrains.rd.util.lifetime.Lifetime
import javax.swing.JButton
import javax.swing.JPanel

class SimpleConsoleProcessorProjectSettingsComponent(val lifetime: Lifetime) {
    private lateinit var panel: DialogPanel
    private val settingViewModel = SimpleConsoleProcessorSettingsViewModel()
    private var consolesPanel = JPanel(VerticalFlowLayout())
    private val consolePanels = mutableListOf<SimpleConsoleCustomizationComponent>()

    fun updateModel(state: SimpleConsoleSettingsState) {
        settingViewModel.updateModel(state)
        consolesPanel.removeAll()
        consolePanels.clear()
        for (consoleViewModel in settingViewModel.consoles) {
            addConsolePanel(consoleViewModel)
        }
    }

    fun getModel(): SimpleConsoleProcessorSettingsViewModel {
        panel.apply()
        for (component in consolePanels) {
            component.apply()
        }
        return settingViewModel
    }

    fun reset() {
        panel.reset()
        for (component in consolePanels) {
            component.reset()
        }
    }

    fun getPanel(): DialogPanel {
        panel = panel {
            row {
                val button = JButton(SimpleConsoleBundle.message("settings.simple.console.add.console.processor.text"))
                button.addActionListener {
                    openCreateCustomConsoleMenu(button)
                }
                button.isOpaque = false
                cell(button)
            }
            row {
                cell(consolesPanel).align(AlignX.FILL)
            }
        }
        for (consoleViewModel in settingViewModel.consoles) {
            addConsolePanel(consoleViewModel)
        }
        panel.reset()
        panel.registerValidators(lifetime.createNestedDisposable())
        return panel
    }

    private fun openCreateCustomConsoleMenu(button: JButton) {
        val actionGroup = DefaultActionGroup()

        if (!settingViewModel.hasConsoleWithId("dotnetSerilog")) {
            actionGroup.add(SimpleAction(".NET: Serilog") {
                val consoleViewModel = settingViewModel.addConsole()
                consoleViewModel.displayName = "Serilog"
                consoleViewModel.id = "dotnetSerilog"
                consoleViewModel.startingLogPattern = "^\\[[\\d:]+ (?<severity>VRB|DBG|INF|WRN|ERR|FTL)\\] (?<message>.+)$"
                consoleViewModel.secondaryLogPattern = "^(?<message>.*)$"
                consoleViewModel.filteringProperties["severity"] = "Severity Level"
                addConsolePanel(consoleViewModel)
            })
        }
        if (!settingViewModel.hasConsoleWithId("dotnetMicrosoftExtensionsLogging")) {
            actionGroup.add(SimpleAction(".NET: Microsoft.Extensions.Logging") {
                val consoleViewModel = settingViewModel.addConsole()
                consoleViewModel.displayName = "Microsoft.Extensions.Logging"
                consoleViewModel.id = "dotnetMicrosoftExtensionsLogging"
                consoleViewModel.startingLogPattern = "^(?<severity>dbug|info|warn|fail|crit): (?<category>.+)$"
                consoleViewModel.secondaryLogPattern = "^ {6}(?<message>.*)$"
                consoleViewModel.filteringProperties["severity"] = "Severity Level"
                consoleViewModel.filteringProperties["category"] = "Category"
                addConsolePanel(consoleViewModel)
            })
        }
        if (!settingViewModel.hasConsoleWithId("dotnetNLog")) {
            actionGroup.add(SimpleAction(".NET: NLog") {
                val consoleViewModel = settingViewModel.addConsole()
                consoleViewModel.displayName = "NLog"
                consoleViewModel.id = "dotnetNLog"
                consoleViewModel.startingLogPattern = "^[\\d- :\\.]+\\|(?<severity>TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\|(?<category>[^|]+)\\|(?<message>.+)$"
                consoleViewModel.secondaryLogPattern = "^(?<message>.*)$"
                consoleViewModel.filteringProperties["severity"] = "Severity Level"
                consoleViewModel.filteringProperties["category"] = "Category"
                addConsolePanel(consoleViewModel)
            })
        }
        actionGroup.add(SimpleAction("Custom") {
            val consoleViewModel = settingViewModel.addConsole()
            consoleViewModel.displayName = "My Awesome Console ${settingViewModel.consoles.size}"
            addConsolePanel(consoleViewModel)
        })
        ActionManager.getInstance().createActionPopupMenu("AwesomeLogViewer_CreateCustomConsole", actionGroup)
            .component
            .show(button, 3, 37)
    }

    private fun addConsolePanel(consoleViewModel: SimpleConsoleConsoleProcessorSettingsViewModel) {
        val component = SimpleConsoleCustomizationComponent(lifetime, consoleViewModel)
        consolesPanel.add(component)
        consolePanels.add(component)

        component.onDelete.advise(lifetime) {
            run {
                consolesPanel.remove(component)
                consolePanels.remove(component)
                settingViewModel.removeConsole(it)
                consolesPanel.revalidate()
            }
        }
        consolesPanel.revalidate()
    }
}
