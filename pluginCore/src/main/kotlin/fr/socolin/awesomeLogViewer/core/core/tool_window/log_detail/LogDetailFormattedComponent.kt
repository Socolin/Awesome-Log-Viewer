package fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail

import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.rd.createNestedDisposable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.unscramble.AnalyzeStacktraceUtil
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.jetbrains.rd.util.AtomicInteger
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryDisplay
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.session.PendingLogEntry
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import io.ktor.util.*
import java.awt.Cursor
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingConstants

class LogDetailFormattedComponent(private val logSession: LogSession) : JBPanel<LogDetailFormattedComponent>() {
    private val builder: TextConsoleBuilder
    private val pluginSettings = GlobalPluginSettingsStorageService.Companion.getInstance()
    private var currentLogLifetime: LifetimeDefinition? = null
    private var _lastLog: LogEntryDisplay? = null

    private val title = JBLabel().apply {
        font = JBFont.h2()
    }

    init {
        layout = GridBagLayout()

        builder = TextConsoleBuilderFactory.getInstance().createBuilder(logSession.project)
        builder.filters(AnalyzeStacktraceUtil.EP_NAME.getExtensions(logSession.project))

        val busConnection = ApplicationManager.getApplication().messageBus.connect(logSession.lifetime.createNestedDisposable())
        busConnection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener { setSelectedLog(_lastLog) })
    }

    fun setSelectedLog(log: LogEntryDisplay?) {
        _lastLog = log;
        removeAll()
        currentLogLifetime?.terminate()
        currentLogLifetime = logSession.lifetime.createNested()
        if (log?.logEntry is PendingLogEntry) {
            add(JBLabel(CoreBundle.message("logs.detail.pending.log.label"), SwingConstants.CENTER).apply { isAllowAutoWrapping = true }, GridBagConstraints().apply {
                gridy = 0
                weightx = 1.0
                anchor = GridBagConstraints.CENTER
                fill = GridBagConstraints.HORIZONTAL
                insets = JBUI.insets(16)
            })
            add(
                JBLabel("<html>" + CoreBundle.message("logs.detail.pending.log.description") + "</html>", SwingConstants.CENTER).apply { isAllowAutoWrapping = true },
                GridBagConstraints().apply {
                    gridy = 1
                    weightx = 1.0
                    anchor = GridBagConstraints.CENTER
                    fill = GridBagConstraints.HORIZONTAL
                    insets = JBUI.insets(16)
                })
            revalidate()
            repaint()
            return
        }
        val formattedLog = log?.getFormattedRenderModel()
        if (formattedLog == null) {
            add(JBLabel(CoreBundle.message("logs.detail.no.log")))
            revalidate()
            return
        }

        title.font = JBFont.h2()
        title.text = formattedLog.title
        val row = AtomicInteger()
        add(title, GridBagConstraints().apply {
            gridy = row.andIncrement
            weightx = 1.0
            anchor = GridBagConstraints.LINE_START
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.insets(8, 0)
        })

        if (formattedLog.stackTrace != null) {
            add(JBLabel("Stacktrace").apply {
                font = JBFont.h3()
            }, GridBagConstraints().apply {
                gridy = row.andIncrement
                weightx = 1.0
                anchor = GridBagConstraints.LINE_START
                fill = GridBagConstraints.HORIZONTAL
                insets = JBUI.insets(4, 16, 4, 0)
            })

            val consoleViewComponent = createConsoleViewComponent(formattedLog.stackTrace)
            add(consoleViewComponent, GridBagConstraints().apply {
                gridy = row.andIncrement
                weightx = 1.0
                weighty = 1.0
                anchor = GridBagConstraints.LINE_START
                fill = GridBagConstraints.BOTH
                insets = JBUI.insets(4, 32, 4, 0)
            })
        }

        for (section in formattedLog.sections) {
            addSectionTitle(section, row)

            for ((key, value) in section.properties) {
                addPropertyLine(key, value, row)
            }
        }
        add(Box.createRigidArea(Dimension(1, 1)), GridBagConstraints().apply {
            gridy = row.andIncrement
            weighty = 1.0
        })
        revalidate()
        repaint()
    }

    private fun addPropertyLine(key: String, value: String, row: AtomicInteger) {
        val propertyPanel = JPanel()
        propertyPanel.layout = GridBagLayout()

        val editorFont = EditorUtil.getEditorFont()
        propertyPanel.add(JBLabel("$key:").apply {
            font = editorFont
        }, GridBagConstraints().apply {
            gridx = 0
            weightx = 0.0
            alignmentY = TOP_ALIGNMENT
            anchor = GridBagConstraints.FIRST_LINE_START
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.insets(4, 0)
        })

        if (value.contains('\n')) {
            propertyPanel.add(createConsoleViewComponent(value), GridBagConstraints().apply {
                gridx = 1
                weightx = 1.0
                anchor = GridBagConstraints.LINE_START
                fill = GridBagConstraints.HORIZONTAL
                insets = JBUI.insets(4, 0)
            })
        } else {
            val valueLabel = JBLabel("<html><a href=''>${value.escapeHTML()}</a></html>").apply {
                font = editorFont
            }
            valueLabel.setCursor(Cursor(Cursor.HAND_CURSOR))
            valueLabel.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    logSession.setTextFilter(value)
                }
            })
            valueLabel.isAllowAutoWrapping = true
            propertyPanel.add(valueLabel, GridBagConstraints().apply {
                gridx = 1
                weightx = 1.0
                anchor = GridBagConstraints.LINE_START
                fill = GridBagConstraints.HORIZONTAL
                insets = JBUI.insets(4, 0)
            })
        }
        add(propertyPanel, GridBagConstraints().apply {
            gridy = row.andIncrement
            weightx = 1.0
            anchor = GridBagConstraints.LINE_START
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.insets(4, 32, 4, 0)
        })
    }

    private fun LogDetailFormattedComponent.addSectionTitle(
        section: FormattedLogSectionModel,
        row: AtomicInteger
    ) {
        add(JBLabel(section.title).apply {
            font = JBFont.h3()
        }, GridBagConstraints().apply {
            gridy = row.andIncrement
            weightx = 1.0
            anchor = GridBagConstraints.LINE_START
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.insets(4, 16, 4, 0)
        })
    }

    private fun createConsoleViewComponent(text: String): JComponent {
        val consoleView: ConsoleView = builder.console
        consoleView.clear()
        consoleView.allowHeavyFilters()
        if (consoleView.component is ConsoleViewImpl) {
            val editor = (consoleView.component as ConsoleViewImpl).editor
            if (editor != null) {
                editor.settings.isUseSoftWraps = pluginSettings.state.useSoftWrap.value
                pluginSettings.state.useSoftWrap.advise(currentLogLifetime!!) {
                    editor.settings.isUseSoftWraps = it
                }
                consoleView.component.minimumSize = Dimension(
                    consoleView.component.minimumSize.width,
                    ((text.count { it == '\n' } + 2) * editor.lineHeight) + 16
                )
                consoleView.component.maximumSize = Dimension(
                    consoleView.component.maximumSize.width,
                    500
                )
            }
        }
        consoleView.print(text, ConsoleViewContentType.NORMAL_OUTPUT)
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
        return consoleView.component
    }
}


data class FormattedLogModel(
    val title: String,
    val stackTrace: String?,
    val sections: List<FormattedLogSectionModel>,
)

data class FormattedLogSectionModel(
    val title: String,
    val properties: List<FormattedLogPropertyModel>,
)

data class FormattedLogPropertyModel(
    val name: String,
    val value: String,
)

fun MutableList<FormattedLogPropertyModel>.addPropertyIfSet(
    name: String,
    value: String?
) {
    if (value == null)
        return
    add(FormattedLogPropertyModel(name, value))
}

fun MutableList<FormattedLogPropertyModel>.addProperties(
    values: Map<String, String>?
) {
    if (values == null)
        return
    for ((name, value) in values) {
        add(FormattedLogPropertyModel(name, value))
    }
}
