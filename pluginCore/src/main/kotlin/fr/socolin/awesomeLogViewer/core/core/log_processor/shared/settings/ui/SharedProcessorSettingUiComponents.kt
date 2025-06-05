package fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.ui.layout.not
import com.intellij.ui.layout.or
import fr.socolin.awesomeLogViewer.core.core.CoreBundle

class SharedProcessorSettingUiComponents {
    companion object {
        fun createActivationSettingSection(
            panel: Panel,
            viewModel: LogProcessorSettingsViewModel<*>
        ): ComponentPredicate {
            var enabled: ComponentPredicate = ComponentPredicate.FALSE
            panel.group(CoreBundle.Companion.message("settings.shared.activation.title")) {
                row {
                    val checkboxRun = checkBox(CoreBundle.Companion.message("settings.shared.activation.run.label"))
                        .bindSelected(viewModel::enableForRun)
                    icon(AllIcons.Actions.Execute)
                    val checkboxDebug = checkBox(CoreBundle.Companion.message("settings.shared.activation.debug.label"))
                        .bindSelected(viewModel::enableForDebug)
                    icon(AllIcons.Actions.StartDebugger)
                    enabled = checkboxRun.selected.or(checkboxDebug.selected)
                }
            }
            return enabled
        }

        fun createConsoleBaseConfigurationSection(
            panel: Panel,
            viewModel: ConsoleLogProcessorSettingsViewModel<*>
        ) {
            panel.group(CoreBundle.Companion.message("settings.shared.output.source.title")) {
                row {
                    checkBox(CoreBundle.Companion.message("settings.shared.output.source.std.label"))
                        .bindSelected(viewModel::readConsoleOutput)
                        .comment(CoreBundle.Companion.message("settings.shared.output.source.std.description"), 30)
                    checkBox(CoreBundle.Companion.message("settings.shared.output.source.debug.label"))
                        .bindSelected(viewModel::readDebugOutput)
                        .comment(CoreBundle.Companion.message("settings.shared.output.source.debug.description"), 30)
                }
            }
        }

        fun createNetworkBaseConfigurationSection(
            panel: Panel,
            viewModel: NetworkLogProcessorSettingsViewModel<*>,
            defaultPort: Int = -1
        ) {
            panel.group(CoreBundle.Companion.message("settings.shared.configuration.network.title")) {
                row {
                    val checkbox = checkBox(CoreBundle.Companion.message("settings.shared.configuration.network.use.random.port.label"))
                        .bindSelected(viewModel::listenToRandomPort)
                    val portTextField = intTextField(1..65535, 1)
                        .bindIntText(viewModel::listenPortNumber)
                        .comment(CoreBundle.Companion.message("settings.shared.configuration.network.port.comment"))
                        .validationOnInput {
                            try {
                                val port = it.text.toInt()
                                if (port in 1..65536) null
                                else ValidationInfo(CoreBundle.Companion.message("settings.shared.configuration.network.port.invalid"))
                            } catch (_: NumberFormatException) {
                                ValidationInfo(CoreBundle.Companion.message("settings.shared.configuration.network.invalid.port.not.number"))
                            }
                        }
                        .visibleIf(checkbox.selected.not())
                    if (defaultPort > 0) {
                        text(CoreBundle.Companion.message("settings.shared.configuration.network.default.port", defaultPort))
                            .visibleIf(checkbox.selected.not())
                    }
                    checkbox.selected.addListener {
                        if (it && viewModel.listenPortNumber == 0 && defaultPort > 0)
                            portTextField.text(defaultPort.toString())
                    }
                }
            }
        }
    }
}
