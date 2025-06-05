package fr.socolin.awesomeLogViewer.core.core.tool_window.toolbar

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.options.ShowSettingsUtil
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.session.LogDisplayMode
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.settings.GlobalPluginsSettingsConfigurable
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.utilities.PluginIcons
import javax.swing.JComponent

class LogsToolbar {
    companion object {
        fun createToolbar(
            logSession: LogSession,
            targetComponent: JComponent
        ): ActionToolbar {
            val actionGroup = DefaultActionGroup()

            val pluginSettings = GlobalPluginSettingsStorageService.Companion.getInstance()

            actionGroup.add(
                SimpleToolbarAction(
                    AllIcons.General.Delete,
                    CoreBundle.Companion.message("logs.toolbar.clear.text"),
                    CoreBundle.Companion.message("logs.toolbar.clear.description")
                ) {
                    logSession.clearLogs()
                })
            actionGroup.add(
                SimpleToolbarAction(
                    AllIcons.General.Locate,
                    CoreBundle.Companion.message("logs.toolbar.locate.text"),
                    CoreBundle.Companion.message("logs.toolbar.locate.description")
                ) {
                    logSession.scrollToSelectedLog()
                })
            actionGroup.add(
                ToggleToolbarAction(
                    AllIcons.Actions.ToggleSoftWrap,
                    CoreBundle.Companion.message("logs.toolbar.soft.wrap.text"),
                    CoreBundle.Companion.message("logs.toolbar.soft.wrap.description"),
                    pluginSettings.state.scrollToEnd.value
                ) {
                    pluginSettings.state.useSoftWrap.set(it)
                })
            actionGroup.add(
                ToggleToolbarAction(
                    AllIcons.RunConfigurations.Scroll_down,
                    CoreBundle.Companion.message("logs.toolbar.scroll.to.end.text"),
                    CoreBundle.Companion.message("logs.toolbar.scroll.to.end.description"),
                    pluginSettings.state.scrollToEnd.value
                ) {
                    pluginSettings.state.scrollToEnd.set(it)
                })
            if (logSession.logProcessor.supportSampling()) {
                actionGroup.addSeparator()
                actionGroup.add(
                    ToggleToolbarAction(
                        AllIcons.Actions.ToggleVisibility,
                        CoreBundle.Companion.message("logs.toolbar.show.sampling.indicator.text"),
                        CoreBundle.Companion.message("logs.toolbar.show.sampling.indicator.description"),
                        pluginSettings.state.showSampledIndicator.value
                    ) {
                        pluginSettings.state.showSampledIndicator.set(it)
                    })
            }
            actionGroup.addSeparator()
            actionGroup.add(
                SimpleToolbarAction(
                    PluginIcons.Misc.ToolWindowTimer,
                    CoreBundle.Companion.message("logs.toolbar.toggle.time.from.start.text"),
                    CoreBundle.Companion.message("logs.toolbar.toggle.time.from.start.description"),
                ) {
                    logSession.pluginSettings.state.showTimeFromStart.set(!logSession.pluginSettings.state.showTimeFromStart.value)
                })
            actionGroup.addSeparator()
            actionGroup.add(
                SimpleToolbarAction(
                    AllIcons.General.Settings,
                    CoreBundle.Companion.message("logs.toolbar.settings.text"),
                    CoreBundle.Companion.message("logs.toolbar.settings.description"),
                ) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(logSession.project, GlobalPluginsSettingsConfigurable::class.java)
                })

            // Add expand / collapse
            return ActionManager.getInstance().createActionToolbar("AwesomeLogViewerToolWindow", actionGroup, false)
                .also { it.targetComponent = targetComponent }
        }
    }
}
