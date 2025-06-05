package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsStorageService

@State(
    name = "fr.socolin.awesomeLogViewer.module.applicationInsights.state",
    storages = [Storage("fr.socolin.awesomeLogViewer.module.applicationInsights.project.xml")]
)
@Service(Service.Level.PROJECT)
class ApplicationInsightsSettingsStorageService : BaseSettingsStorageService<ApplicationInsightsSettingsState>(
    ApplicationInsightsSettingsState()
), Disposable {
    companion object {
        fun getInstance(project: Project): ApplicationInsightsSettingsStorageService =
            project.getService(ApplicationInsightsSettingsStorageService::class.java)
    }
}
