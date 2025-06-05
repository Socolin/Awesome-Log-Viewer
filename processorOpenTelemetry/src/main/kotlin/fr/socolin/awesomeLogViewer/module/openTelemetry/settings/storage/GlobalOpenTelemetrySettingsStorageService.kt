package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsStorageService

@State(
    name = "fr.socolin.awesomeLogViewer.module.openTelemetry.state.global",
    storages = [Storage("fr.socolin.awesomeLogViewer.module.openTelemetry.global.xml")]
)
@Service(Service.Level.APP)
class GlobalOpenTelemetrySettingsStorageService : BaseSettingsStorageService<GlobalOpenTelemetrySettingsState>(
    GlobalOpenTelemetrySettingsState()
), Disposable {
    companion object {
        fun getInstance(): GlobalOpenTelemetrySettingsStorageService {
            return ApplicationManager.getApplication().getService(GlobalOpenTelemetrySettingsStorageService::class.java)
        }
    }
}
