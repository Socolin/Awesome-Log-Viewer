package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsStorageService

@State(
    name = "fr.socolin.awesomeLogViewer.module.simpleConsole.state",
    storages = [Storage("fr.socolin.awesomeLogViewer.module.simpleConsole.project.xml")]
)
@Service(Service.Level.PROJECT)
class SimpleConsoleSettingsStorageService : BaseSettingsStorageService<SimpleConsoleSettingsState>(
    SimpleConsoleSettingsState()
), Disposable {
    companion object {
        fun getInstance(project: Project): SimpleConsoleSettingsStorageService =
            project.getService(SimpleConsoleSettingsStorageService::class.java)
    }
}
