package fr.socolin.awesomeLogViewer.core.core.tool_window

import com.intellij.ui.components.JBPanel
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.tool_window.toolbar.LogsToolbar
import java.awt.GridBagConstraints
import java.awt.GridBagLayout


class PluginToolWindow(
    logSession: LogSession
) : JBPanel<PluginToolWindow>() {
    init {
        layout = GridBagLayout()

        val toolbar = LogsToolbar.Companion.createToolbar(logSession, this)
        add(toolbar.component, GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
            gridheight = 10
            weighty = 1.0
            anchor = GridBagConstraints.FIRST_LINE_START
            fill = GridBagConstraints.VERTICAL
        })
        add(LogSectionPanel(logSession), GridBagConstraints().apply {
            gridwidth = 1
            gridheight = 1
            gridx = 1
            gridy = 1
            fill = GridBagConstraints.BOTH
            weightx = 1.0
            weighty = 1.0
        })
    }

}

