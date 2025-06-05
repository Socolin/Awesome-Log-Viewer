package fr.socolin.awesomeLogViewer.core.core.tool_window.log_detail

import com.intellij.codeInsight.folding.CodeFoldingManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.text.CodeFoldingState
import com.intellij.ui.LanguageTextField.SimpleDocumentCreator
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.ui.UIUtil
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.session.LogSession
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JPanel

class LogDetailComponent(
    logSession: LogSession,
) : JBPanel<LogDetailComponent>() {
    private var document: Document
    private var previewEditor: Editor
    private val pluginSettings = GlobalPluginSettingsStorageService.Companion.getInstance()

    init {
        layout = BorderLayout()


        document = SimpleDocumentCreator().createDocument("", logSession.getRawLogLanguage(), logSession.project)
        ReadAction.nonBlocking<CodeFoldingState?> {
            CodeFoldingManager.getInstance(
                logSession.project
            ).buildInitialFoldings(document)
        }.submit(AppExecutorUtil.getAppExecutorService()).get()

        previewEditor = EditorFactory.getInstance().createViewer(document, logSession.project, EditorKind.MAIN_EDITOR)
        previewEditor.settings.isIndentGuidesShown = true
        previewEditor.settings.additionalLinesCount = 3
        previewEditor.settings.isFoldingOutlineShown = true
        previewEditor.settings.isUseSoftWraps = pluginSettings.state.useSoftWrap.value

        pluginSettings.state.useSoftWrap.advise(logSession.lifetime) {
            previewEditor.settings.isUseSoftWraps = it
        }

        if (previewEditor is EditorEx) {
            val fileType = logSession.getRawLogLanguage()?.associatedFileType
            if (fileType != null) {
                (previewEditor as EditorEx).highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(logSession.project, fileType)
            }
            (previewEditor as EditorEx).foldingModel.isFoldingEnabled = true
        }

        val logDetailFormattedComponent = LogDetailFormattedComponent(logSession)
        val logDetailFormattedScrollPane = JBScrollPane(
            logDetailFormattedComponent,
            JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        )
        val logDetailFormattedPanel = JPanel()
        logDetailFormattedPanel.layout = GridBagLayout()
        logDetailFormattedPanel.add(logDetailFormattedScrollPane, GridBagConstraints().apply {
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        })
        val tabbedPane = JBTabbedPane()
        tabbedPane.addTab(CoreBundle.Companion.message("logs.detail.formatted.tab.name"), AllIcons.General.Information, logDetailFormattedScrollPane)
        tabbedPane.addTab(CoreBundle.Companion.message("logs.detail.raw.tab.name"), AllIcons.FileTypes.Json, previewEditor.component)
        add(tabbedPane)

        logSession.selectedLog.advise(logSession.lifetime) {
            val rawLog = it?.logEntry?.getFormattedRawLog() ?: ""
            UIUtil.invokeLaterIfNeeded {
                logDetailFormattedComponent.setSelectedLog(it)
                WriteAction.run<Throwable> {
                    document.setText(rawLog)
                    FileDocumentManager.getInstance().saveDocument(document)
                    CodeFoldingManager.getInstance(logSession.project).updateFoldRegions(previewEditor)
                }
            }
        }
    }
}
