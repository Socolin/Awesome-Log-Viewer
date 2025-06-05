package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.ui

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.SimpleConsoleSettingsStorageService
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class SimpleConsoleProcessorSettingsConfigurable(project: Project) : SearchableConfigurable {
    private var settingsComponent: SimpleConsoleProcessorProjectSettingsComponent? = null
    private val lifetime: LifetimeDefinition = LifetimeDefinition()
    private val simpleConsoleSettings = SimpleConsoleSettingsStorageService.Companion.getInstance(project)

    override fun getDisplayName(): @ConfigurableName String {
        return "Simple Console"
    }

    override fun getId(): @NonNls String {
        return "fr.socolin.awesomeLogViewer.core.settings.processor.SimpleConsole"
    }

    override fun createComponent(): JComponent {
        val settingsComponent = SimpleConsoleProcessorProjectSettingsComponent(lifetime)
        settingsComponent.updateModel(simpleConsoleSettings.state)
        this.settingsComponent = settingsComponent
        return settingsComponent.getPanel()
    }

    override fun disposeUIResources() {
        lifetime.terminate()
    }

    override fun isModified(): Boolean {
        val settingsComponent = settingsComponent ?: return false

        var modified = false
        settingsComponent.getModel()
        modified = modified or !settingsComponent.getModel().settingsEquals(simpleConsoleSettings.state)
        return modified
    }

    override fun apply() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.getModel().applyChangesTo(simpleConsoleSettings.state)
    }

    override fun reset() {
        val settingsComponent = settingsComponent ?: return
        settingsComponent.updateModel(simpleConsoleSettings.state)
        settingsComponent.reset()
    }
}
