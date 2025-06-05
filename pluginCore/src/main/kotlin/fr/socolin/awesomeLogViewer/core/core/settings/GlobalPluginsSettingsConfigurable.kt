package fr.socolin.awesomeLogViewer.core.core.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import fr.socolin.awesomeLogViewer.core.core.settings.ui.GlobalPluginProjectSettingsComponent
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class GlobalPluginsSettingsConfigurable() : SearchableConfigurable {
    private var settingsComponent: GlobalPluginProjectSettingsComponent? = null
    private val lifetime: LifetimeDefinition = LifetimeDefinition()
    private val pluginSettings = GlobalPluginSettingsStorageService.Companion.getInstance()

    override fun getDisplayName(): @ConfigurableName String {
        return "Awesome Log Viewer"
    }

    override fun getId(): @NonNls String {
        return "fr.socolin.awesomeLogViewer.core.settings.ProjectSettingsConfigurable"
    }

    override fun createComponent(): JComponent {
        val settingsComponent = GlobalPluginProjectSettingsComponent()
        settingsComponent.updateModel(pluginSettings.state)
        this.settingsComponent = settingsComponent
        return settingsComponent.getPanel()
    }

    override fun disposeUIResources() {
        lifetime.terminate()
    }

    override fun isModified(): Boolean {
        val settingsComponent = settingsComponent ?: return false

        var modified = false
        modified = modified or !pluginSettings.state.areSettingsEquals(settingsComponent.getModel())
        return modified
    }

    override fun apply() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.getModel().applyChangesTo(pluginSettings.state)
    }

    override fun reset() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.updateModel(pluginSettings.state)
        settingsComponent.reset()
    }
}
