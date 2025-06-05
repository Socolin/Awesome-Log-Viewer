package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.rd.createNestedDisposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ColorPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.SharedProcessorSettingUiComponents
import fr.socolin.awesomeLogViewer.core.core.session.debug.DebugOutputHelper
import fr.socolin.awesomeLogViewer.core.core.settings.ui.GlobalPluginProjectSettingsComponent.Companion.editorFont
import fr.socolin.awesomeLogViewer.module.openTelemetry.OpenTelemetryBundle
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.OpenTelemetryDefaultColors
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.GlobalOpenTelemetrySettingsState
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetrySettingsState
import com.jetbrains.rd.util.lifetime.Lifetime
import fleet.util.max
import java.awt.Color

class OpenTelemetryProcessorProjectSettingsComponent(
    private val lifetime: Lifetime
) {
    private lateinit var panel: DialogPanel
    private val settingViewModel = OpenTelemetryProcessorSettingsViewModel()

    fun updateModel(state: OpenTelemetrySettingsState) {
        settingViewModel.updateModel(state)
    }

    fun updateModel(globalState: GlobalOpenTelemetrySettingsState) {
        settingViewModel.updateModel(globalState)
    }

    fun getModel(): OpenTelemetryProcessorSettingsViewModel {
        panel.apply()
        return settingViewModel
    }

    fun getPanel(): DialogPanel {
        panel = panel {
            group(OpenTelemetryBundle.Companion.message("settings.open.telemetry.console.name")) {
                row {
                    text(OpenTelemetryBundle.Companion.message("settings.open.telemetry.console.description"))
                }
                val enabled = SharedProcessorSettingUiComponents.createActivationSettingSection(
                    this,
                    settingViewModel.console
                )
                if (DebugOutputHelper.isDebugOutputSupported()) {
                    panel {
                        SharedProcessorSettingUiComponents.createConsoleBaseConfigurationSection(
                            this,
                            settingViewModel.console
                        )
                    }.enabledIf(enabled)
                }
            }
            group(OpenTelemetryBundle.Companion.message("settings.open.telemetry.network.name")) {
                row {
                    text(OpenTelemetryBundle.Companion.message("settings.open.telemetry.network.description"))
                }
                val enabled = SharedProcessorSettingUiComponents.createActivationSettingSection(
                    this,
                    settingViewModel.network
                )
                panel {
                    SharedProcessorSettingUiComponents.createNetworkBaseConfigurationSection(
                        this,
                        settingViewModel.network,
                        4317
                    )
                }.enabledIf(enabled)
                group(OpenTelemetryBundle.Companion.message("settings.open.telemetry.network.environment.variables.title")) {
                    row {
                        textArea()
                            .rows(max(settingViewModel.network.environmentVariables.size, 5))
                            .resizableColumn()
                            .align(AlignX.FILL)
                            .bindText(
                                MutableProperty(
                                    { settingViewModel.network.environmentVariables.entries.joinToString("\n") { "${it.key}=${it.value}" } },
                                    {
                                        settingViewModel.network.environmentVariables =
                                            it.trim().split('\n').associate { line -> line.substringBefore('=').trim() to line.substringAfter('=').trim() }
                                                .toMutableMap()
                                    }
                                )
                            )
                            .comment(OpenTelemetryBundle.Companion.message("settings.open.telemetry.network.environment.variables.comment"))
                    }
                }
            }
            group(OpenTelemetryBundle.Companion.message("settings.open.telemetry.color.title")) {
                addColorSelector(
                    "Metric",
                    MutableProperty(
                        { settingViewModel.metricColor },
                        { settingViewModel.metricColor = it ?: OpenTelemetryDefaultColors.Companion.metricColor }
                    )
                ) { settingViewModel.metricColor = OpenTelemetryDefaultColors.Companion.metricColor }
                addColorSelector(
                    "Trace",
                    MutableProperty(
                        { settingViewModel.traceColor },
                        { settingViewModel.traceColor = it ?: OpenTelemetryDefaultColors.Companion.traceColor }
                    )
                ) { settingViewModel.traceColor = OpenTelemetryDefaultColors.Companion.traceColor }
                addColorSelector(
                    "Log Record",
                    MutableProperty(
                        { settingViewModel.logRecordColor },
                        { settingViewModel.logRecordColor = it ?: OpenTelemetryDefaultColors.Companion.logRecordColor }
                    )
                ) { settingViewModel.logRecordColor = OpenTelemetryDefaultColors.Companion.logRecordColor }
            }
        }

        panel.reset()
        panel.registerValidators(lifetime.createNestedDisposable())
        return panel
    }

    private fun Panel.addColorSelector(name: String, property: MutableProperty<Color?>, resetAction: () -> Unit) {
        row {
            cell(ColorPanel())
                .label(JBLabel(name).apply {
                    font = editorFont
                })
                .bind(
                    ColorPanel::getSelectedColor,
                    ColorPanel::setSelectedColor,
                    property
                )
            val resetColorAction = object :
                DumbAwareAction(
                    CoreBundle.message("settings.severity.reset.color"),
                    CoreBundle.message("settings.severity.reset.color.description"),
                    AllIcons.General.Reset
                ) {
                override fun actionPerformed(e: AnActionEvent) {
                    resetAction.invoke()
                    panel.reset()
                }
            }
            actionButton(resetColorAction)
        }.layout(RowLayout.PARENT_GRID)
    }
    fun reset() {
        panel.reset()
    }
}
