package fr.socolin.awesomeLogViewer.core.core.tool_window

import com.intellij.openapi.ui.Splitter
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBPanel
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.tool_window.filter.FilterSectionPanel
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail.LogDetailComponent
import fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.LogListComponent
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

class LogSectionPanel(
    logSession: LogSession
) : JBPanel<LogSectionPanel>() {
    private val pluginSettings = GlobalPluginSettingsStorageService.Companion.getInstance()

    init {
        layout = GridBagLayout()

        val splitPane = JBSplitter(false, pluginSettings.state.logListSplitProportion.value)
        splitPane.addPropertyChangeListener(Splitter.PROP_PROPORTION) {
            pluginSettings.state.logListSplitProportion.value = it.newValue as Float
        }

        add(splitPane, GridBagConstraints().apply {
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        })

        val panel = JPanel()
        panel.layout = GridBagLayout()
        panel.add(FilterSectionPanel(logSession), GridBagConstraints().apply {
            gridy = 0
            weightx = 1.0
            weighty = 0.0
            anchor = GridBagConstraints.PAGE_START
            fill = GridBagConstraints.HORIZONTAL
        })
        panel.add(LogListComponent(logSession), GridBagConstraints().apply {
            gridy = 1
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
            anchor = GridBagConstraints.PAGE_START
        })

        splitPane.firstComponent = panel
        splitPane.secondComponent = LogDetailComponent(logSession)
    }
}
