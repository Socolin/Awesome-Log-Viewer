package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.rd.createNestedDisposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.*
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.ui.SharedProcessorSettingUiComponents
import fr.socolin.awesomeLogViewer.core.core.session.debug.DebugOutputHelper
import fr.socolin.awesomeLogViewer.module.simpleConsole.LogParsingMethod
import fr.socolin.awesomeLogViewer.module.simpleConsole.SimpleConsoleBundle
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.Signal
import fleet.util.max
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import java.awt.GridLayout
import java.util.regex.Pattern

class SimpleConsoleCustomizationComponent(
    lifetime: Lifetime,
    consoleViewModel: SimpleConsoleConsoleProcessorSettingsViewModel
) : JBPanel<SimpleConsoleCustomizationComponent>(GridLayout()) {
    private val panel: DialogPanel
    val onDelete = Signal<SimpleConsoleConsoleProcessorSettingsViewModel>()

    init {
        val deleteAction = object : DumbAwareAction(
            SimpleConsoleBundle.message("settings.simple.console.action.delete.processor.text"),
            SimpleConsoleBundle.message("settings.simple.console.action.delete.processor.description"),
            AllIcons.General.Delete
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                onDelete.fire(consoleViewModel)
            }
        }
        panel = panel {
            collapsibleGroup(consoleViewModel.displayName) {
                group(SimpleConsoleBundle.message("settings.simple.console.processor.general")) {
                    row {
                        textField()
                            .resizableColumn()
                            .align(AlignX.FILL)
                            .label(SimpleConsoleBundle.message("settings.simple.console.processor.display.name"))
                            .bindText(consoleViewModel::displayName)
                        textField()
                            .resizableColumn()
                            .align(AlignX.FILL)
                            .label(SimpleConsoleBundle.message("settings.simple.console.processor.id"))
                            .bindText(consoleViewModel::id)
                        actionButton(deleteAction)
                    }
                }
                val enabled = SharedProcessorSettingUiComponents.createActivationSettingSection(
                    this,
                    consoleViewModel
                )
                if (DebugOutputHelper.isDebugOutputSupported()) {
                    panel {
                        SharedProcessorSettingUiComponents.createConsoleBaseConfigurationSection(
                            this,
                            consoleViewModel
                        )
                    }.enabledIf(enabled)
                }
                group(SimpleConsoleBundle.message("settings.simple.console.environment.variables.title")) {
                    row {
                        textArea()
                            .enabled(true)
                            .rows(max(consoleViewModel.environmentVariables.size, 3))
                            .resizableColumn()
                            .align(AlignX.FILL)
                            .bindText(
                                MutableProperty(
                                    { consoleViewModel.environmentVariables.entries.joinToString("\n") { "${it.key}=${it.value}" } },
                                    { consoleViewModel.environmentVariables= it.trim().split('\n').associate { line -> line.substringBefore('=').trim() to line.substringAfter('=').trim()}.toMutableMap()}
                                )
                            )
                            .comment(SimpleConsoleBundle.message("settings.simple.console.environment.variables.comment"))
                    }
                }
                group(SimpleConsoleBundle.message("settings.simple.console.processor.advanced")) {
                    group(SimpleConsoleBundle.message("settings.simple.console.processor.log.parsing.title")) {
                        lateinit var useRegexButton: Cell<JBRadioButton>
                        buttonsGroup {
                            row(SimpleConsoleBundle.message("settings.simple.console.processor.log.parsing.method.title")) {
                                radioButton(SimpleConsoleBundle.message("settings.simple.console.processor.use.json.button.label"), LogParsingMethod.JSON)
                                useRegexButton = radioButton(SimpleConsoleBundle.message("settings.simple.console.processor.use.regex.button.label"), LogParsingMethod.Regex)
                            }
                        }.bind(consoleViewModel::parsingMethod)
                        group(SimpleConsoleBundle.message("settings.simple.console.processor.log.pattern.title")) {
                            row {
                                checkBox(SimpleConsoleBundle.message("settings.simple.console.processor.remove.ansi"))
                                    .bindSelected(consoleViewModel::removeAnsiColorCode)
                            }
                            row {
                                textField()
                                    .label(SimpleConsoleBundle.message("settings.simple.console.processor.log.start.pattern"))
                                    .resizableColumn()
                                    .align(AlignX.FILL)
                                    .validationOnInput {
                                        try {
                                            Pattern.compile(it.text)
                                            null
                                        } catch (e: Exception) {
                                            ValidationInfo(SimpleConsoleBundle.message("settings.simple.console.processor.invalid.regex", e.message ?: ""))
                                        }
                                    }
                                    .bindText(consoleViewModel::startingLogPattern)
                                    .comment(SimpleConsoleBundle.message("settings.simple.console.processor.log.start.pattern.description"))
                            }
                            row {
                                val multilineCheckBox = checkBox(SimpleConsoleBundle.message("settings.simple.console.processor.multiline"))
                                    .bindSelected(
                                        MutableProperty(
                                            { !consoleViewModel.secondaryLogPattern.isEmpty() },
                                            { consoleViewModel.secondaryLogPattern = if (it) "" else "^(?<message>.*)$" }
                                        ))
                                textField()
                                    .label(SimpleConsoleBundle.message("settings.simple.console.processor.next.lines.pattern"))
                                    .resizableColumn()
                                    .validationOnInput {
                                        try {
                                            Pattern.compile(it.text)
                                            null
                                        } catch (e: Exception) {
                                            ValidationInfo(SimpleConsoleBundle.message("settings.simple.console.processor.invalid.regex", e.message ?: ""))
                                        }
                                    }
                                    .bindText(consoleViewModel::secondaryLogPattern)
                                    .visibleIf(multilineCheckBox.selected)
                            }
                        }.visibleIf(useRegexButton.selected)
                    }
                    group(SimpleConsoleBundle.message("settings.simple.console.processor.filtering.properties")) {
                        row {
                            textArea()
                                .rows(5)
                                .resizableColumn()
                                .align(AlignX.FILL)
                                .bindText(
                                    MutableProperty(
                                        { consoleViewModel.filteringProperties.entries.joinToString("\n") { "${it.key}: ${it.value}" } },
                                        { consoleViewModel.filteringProperties = it.trim().split('\n').associate { line -> line.substringBefore(':').trim() to line.substringAfter(':').trim()}.toMutableMap()}
                                    )
                                )
                                .comment(SimpleConsoleBundle.message("settings.simple.console.processor.properties.comment"))
                        }
                    }
                }
            }
        }
        panel.reset()
        panel.registerValidators(lifetime.createNestedDisposable())
        add(panel)
    }

    fun apply() {
        panel.apply()
    }

    fun reset() {
        panel.reset()
    }
}
