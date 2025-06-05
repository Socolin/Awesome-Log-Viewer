package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.ui

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.ApplicationInsightsSettingsStorageService
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage.GlobalApplicationInsightsSettingsStorageService
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class ApplicationInsightsProcessorSettingsConfigurable(private val project: Project) : SearchableConfigurable {
    private var settingsComponent: ApplicationInsightsProcessorProjectSettingsComponent? = null
    private val lifetime: LifetimeDefinition = LifetimeDefinition()
    private val applicationInsightsSettings = ApplicationInsightsSettingsStorageService.Companion.getInstance(project)
    private val globalApplicationInsightsSettings = GlobalApplicationInsightsSettingsStorageService.Companion.getInstance()

    override fun getDisplayName(): @ConfigurableName String {
        return "Application Insights"
    }

    override fun getId(): @NonNls String {
        return "fr.socolin.awesomeLogViewer.core.settings.processor.ApplicationInsights"
    }

    override fun createComponent(): JComponent {
        val settingsComponent = ApplicationInsightsProcessorProjectSettingsComponent(lifetime)
        settingsComponent.updateModel(applicationInsightsSettings.state)
        settingsComponent.updateModel(globalApplicationInsightsSettings.state)
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
        modified = modified or !viewModel.settingsEquals(applicationInsightsSettings.state)
        modified = modified or !viewModel.settingsEquals(globalApplicationInsightsSettings.state)
        return modified
    }

    override fun apply() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.getModel().applyChangesTo(applicationInsightsSettings.state)
        settingsComponent.getModel().applyChangesTo(globalApplicationInsightsSettings.state)
    }

    override fun reset() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.updateModel(applicationInsightsSettings.state)
        settingsComponent.updateModel(globalApplicationInsightsSettings.state)
        settingsComponent.reset()
    }
}
