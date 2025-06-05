package fr.socolin.awesomeLogViewer.core.core.settings.storage

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

// https://plugins.jetbrains.com/docs/intellij/settings-tutorial.html#the-appsettingscomponent-class
@State(
    name = "fr.socolin.awesomeLogViewer.storage.GlobalPluginSettingsStorageService",
    storages = [Storage("fr.socolin.awesomeLogViewer.global.xml")]
)
@Service(Service.Level.APP)
class GlobalPluginSettingsStorageService() :
    BaseSettingsStorageService<GlobalPluginSettingsState>(
        GlobalPluginSettingsState()
    ) {

    companion object {
        fun getInstance(): GlobalPluginSettingsStorageService {
            return ApplicationManager.getApplication().getService(GlobalPluginSettingsStorageService::class.java)
        }
    }
}


