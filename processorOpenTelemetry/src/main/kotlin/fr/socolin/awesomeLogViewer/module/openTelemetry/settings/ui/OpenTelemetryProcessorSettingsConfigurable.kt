package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.ui

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.GlobalOpenTelemetrySettingsStorageService
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage.OpenTelemetrySettingsStorageService
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class OpenTelemetryProcessorSettingsConfigurable(private val project: Project) : SearchableConfigurable {
    private var settingsComponent: OpenTelemetryProcessorProjectSettingsComponent? = null
    private val lifetime: LifetimeDefinition = LifetimeDefinition()
    private val openTelemetrySettings = OpenTelemetrySettingsStorageService.Companion.getInstance(project)
    private val globalOpenTelemetrySettings = GlobalOpenTelemetrySettingsStorageService.Companion.getInstance()

    override fun getDisplayName(): @ConfigurableName String {
        return "Open Telemetry"
    }

    override fun getId(): @NonNls String {
        return "fr.socolin.awesomeLogViewer.core.settings.processor.OpenTelemetry"
    }

    override fun createComponent(): JComponent {
        val settingsComponent = OpenTelemetryProcessorProjectSettingsComponent(lifetime)
        settingsComponent.updateModel(openTelemetrySettings.state)
        settingsComponent.updateModel(globalOpenTelemetrySettings.state)
        this.settingsComponent = settingsComponent
        return settingsComponent.getPanel()
    }

    override fun disposeUIResources() {
        lifetime.terminate()
    }

    override fun isModified(): Boolean {
        val settingsComponent = settingsComponent ?: return false

        var modified = false
        val viewModel = settingsComponent.getModel()
        modified = modified or !viewModel.settingsEquals(openTelemetrySettings.state)
        modified = modified or !viewModel.settingsEquals(globalOpenTelemetrySettings.state)
        return modified
    }

    override fun apply() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.getModel().applyChangesTo(openTelemetrySettings.state)
        settingsComponent.getModel().applyChangesTo(globalOpenTelemetrySettings.state)
    }

    override fun reset() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.updateModel(openTelemetrySettings.state)
        settingsComponent.updateModel(globalOpenTelemetrySettings.state)
        settingsComponent.reset()
    }
}
