package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.ui

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
import fr.socolin.awesomeLogViewer.module.applicationInsights.ApplicationInsightsBundle
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.ApplicationInsightsDefaultColors
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.GlobalApplicationInsightsSettingsState
import com.jetbrains.rd.util.lifetime.Lifetime
import fleet.util.max
import java.awt.Color

class ApplicationInsightsProcessorProjectSettingsComponent(
    private val lifetime: Lifetime
) {
    private lateinit var panel: DialogPanel
    private val settingViewModel = ApplicationInsightsProcessorSettingsViewModel()

    fun updateModel(state: ApplicationInsightsSettingsState) {
        settingViewModel.updateModel(state)
    }

    fun updateModel(state: GlobalApplicationInsightsSettingsState) {
        settingViewModel.updateModel(state)
    }

    fun getModel(): ApplicationInsightsProcessorSettingsViewModel {
        panel.apply()
        return settingViewModel
    }

    fun getPanel(): DialogPanel {
        panel = panel {
            group(ApplicationInsightsBundle.Companion.message("settings.application.insights.console.name")) {
                row {
                    text(ApplicationInsightsBundle.Companion.message("settings.application.insights.console.description"))
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
            group(ApplicationInsightsBundle.Companion.message("settings.application.insights.network.name")) {
                row {
                    text(ApplicationInsightsBundle.Companion.message("settings.application.insights.network.description"))
                }
                val enabled = SharedProcessorSettingUiComponents.createActivationSettingSection(
                    this,
                    settingViewModel.network
                )
                panel {
                    SharedProcessorSettingUiComponents.createNetworkBaseConfigurationSection(
                        this,
                        settingViewModel.network
                    )
                }.enabledIf(enabled)
            }
            group(ApplicationInsightsBundle.Companion.message("settings.application.insights.network.environment.variables.title")) {
                row {
                    textArea()
                        .rows(max(settingViewModel.network.environmentVariables.size, 3))
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
                        .comment(ApplicationInsightsBundle.Companion.message("settings.application.insights.network.environment.variables.comment"))
                }
            }

            group(ApplicationInsightsBundle.Companion.message("settings.application.insights.color.title")) {
                addColorSelector(
                    "Metric",
                    MutableProperty(
                        { settingViewModel.metricColor },
                        { settingViewModel.metricColor = it ?: ApplicationInsightsDefaultColors.Companion.metricColor }
                    )
                ) { settingViewModel.metricColor = ApplicationInsightsDefaultColors.Companion.metricColor }
                addColorSelector(
                    "Exception",
                    MutableProperty(
                        { settingViewModel.exceptionColor },
                        { settingViewModel.exceptionColor = it ?: ApplicationInsightsDefaultColors.Companion.exceptionColor }
                    )
                ) { settingViewModel.exceptionColor = ApplicationInsightsDefaultColors.Companion.exceptionColor }
                addColorSelector(
                    "Message",
                    MutableProperty(
                        { settingViewModel.messageColor },
                        { settingViewModel.messageColor = it ?: ApplicationInsightsDefaultColors.Companion.messageColor }
                    )
                ) { settingViewModel.messageColor = ApplicationInsightsDefaultColors.Companion.messageColor }
                addColorSelector(
                    "Dependency",
                    MutableProperty(
                        { settingViewModel.dependencyColor },
                        { settingViewModel.dependencyColor = it ?: ApplicationInsightsDefaultColors.Companion.dependencyColor }
                    )
                ) { settingViewModel.dependencyColor = ApplicationInsightsDefaultColors.Companion.dependencyColor }
                addColorSelector(
                    "Request",
                    MutableProperty(
                        { settingViewModel.requestColor },
                        { settingViewModel.requestColor = it ?: ApplicationInsightsDefaultColors.Companion.requestColor }
                    )
                ) { settingViewModel.requestColor = ApplicationInsightsDefaultColors.Companion.requestColor }
                addColorSelector(
                    "Event",
                    MutableProperty(
                        { settingViewModel.eventColor },
                        { settingViewModel.eventColor = it ?: ApplicationInsightsDefaultColors.Companion.eventColor }
                    )
                ) { settingViewModel.eventColor = ApplicationInsightsDefaultColors.Companion.eventColor }
                addColorSelector(
                    "PageView",
                    MutableProperty(
                        { settingViewModel.pageViewColor },
                        { settingViewModel.pageViewColor = it ?: ApplicationInsightsDefaultColors.Companion.pageViewColor }
                    )
                ) { settingViewModel.pageViewColor = ApplicationInsightsDefaultColors.Companion.pageViewColor }

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
