package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsStorageService

@State(
    name = "fr.socolin.awesomeLogViewer.module.applicationInsights.state.global",
    storages = [Storage("fr.socolin.awesomeLogViewer.module.applicationInsights.global.xml")]
)
@Service(Service.Level.APP)
class GlobalApplicationInsightsSettingsStorageService : BaseSettingsStorageService<GlobalApplicationInsightsSettingsState>(
    GlobalApplicationInsightsSettingsState()
), Disposable {
    companion object {
        fun getInstance(): GlobalApplicationInsightsSettingsStorageService {
            return ApplicationManager.getApplication().getService(GlobalApplicationInsightsSettingsStorageService::class.java)
        }
    }
}
