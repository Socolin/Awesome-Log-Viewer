package fr.socolin.awesomeLogViewer.core.core.settings.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.util.not
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.JBUI
import com.jetbrains.rd.util.reactive.Property
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsState
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import java.awt.Font
import kotlin.properties.ObservableProperty

class GlobalPluginProjectSettingsComponent {
    private lateinit var panel: DialogPanel
    private val settingViewModel = GlobalPluginSettingsViewModel()

    fun updateModel(state: GlobalPluginSettingsState) {
        settingViewModel.updateModel(state)
    }

    fun getModel(): GlobalPluginSettingsViewModel {
        panel.apply()
        return settingViewModel
    }

    fun getPanel(): DialogPanel {
        val activateLicenseRequested = AtomicBooleanProperty(false)
        panel = panel {
            group(CoreBundle.Companion.message("settings.logs.group")) {
                row {
                    intTextField(range = 0..1_000_000)
                        .label(CoreBundle.Companion.message("settings.logs.max.count"))
                        .bindIntText(settingViewModel::maxLogCount)
                }
                row {
                    checkBox(CoreBundle.Companion.message("settings.logs.show.time.from.start"))
                        .bindSelected(settingViewModel::showTimeFromStart)
                }
                row {
                    intTextField(range = 12..100)
                        .label(CoreBundle.Companion.message("settings.logs.line.height"))
                        .bindIntText(settingViewModel::logLineHeight)
                }
            }
            group(CoreBundle.Companion.message("settings.severity.level.group")) {
                lateinit var colorizeLogCheckbox: Cell<JBCheckBox>
                row {
                    colorizeLogCheckbox = checkBox(CoreBundle.Companion.message("settings.severity.colorize.log"))
                        .bindSelected(settingViewModel::colorLogBasedOnSeverity)
                }
                group(CoreBundle.Companion.message("settings.severity.colors.group")) {
                    for (level in GenericSeverityLevel.entries) {
                        row {
                            cell(ColorPanel())
                                .label(JBLabel(level.toString()).apply {
                                    font = editorFont
                                })
                                .bind(
                                    ColorPanel::getSelectedColor,
                                    ColorPanel::setSelectedColor,
                                    MutableProperty(
                                        { settingViewModel.colorPerSeverity[level] },
                                        { settingViewModel.colorPerSeverity[level] = if (it != null) JBColor(it, it) else null }
                                    )
                                )

                            val clearColorAction = object :
                                DumbAwareAction(
                                    CoreBundle.Companion.message("settings.severity.remove.color"),
                                    CoreBundle.Companion.message("settings.severity.remove.color.description"),
                                    AllIcons.Actions.ClearCash
                                ) {
                                override fun actionPerformed(e: AnActionEvent) {
                                    settingViewModel.colorPerSeverity[level] = null
                                    panel.reset()
                                }
                            }
                            actionButton(clearColorAction)
                            val resetColorAction = object :
                                DumbAwareAction(
                                    CoreBundle.Companion.message("settings.severity.reset.color"),
                                    CoreBundle.Companion.message("settings.severity.reset.color.description"),
                                    AllIcons.General.Reset
                                ) {
                                override fun actionPerformed(e: AnActionEvent) {
                                    settingViewModel.colorPerSeverity[level] = level.defaultColor
                                    panel.reset()
                                }
                            }
                            actionButton(resetColorAction)
                        }.layout(RowLayout.PARENT_GRID)
                    }
                }.visibleIf(colorizeLogCheckbox.selected)
            }
        }
        return panel
    }

    fun reset() {
        panel.reset()
    }

    companion object {
        val editorFont: Font? = EditorUtil.getEditorFont(JBUI.Fonts.label().size)
    }
}
