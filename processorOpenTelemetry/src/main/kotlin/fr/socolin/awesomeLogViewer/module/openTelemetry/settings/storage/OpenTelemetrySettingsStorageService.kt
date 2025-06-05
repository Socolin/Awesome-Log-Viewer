package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsStorageService

@State(
    name = "fr.socolin.awesomeLogViewer.module.openTelemetry.state",
    storages = [Storage("fr.socolin.awesomeLogViewer.module.openTelemetry.project.xml")]
)
@Service(Service.Level.PROJECT)
class OpenTelemetrySettingsStorageService : BaseSettingsStorageService<OpenTelemetrySettingsState>(
    OpenTelemetrySettingsState()
), Disposable {
    companion object {
        fun getInstance(project: Project): OpenTelemetrySettingsStorageService =
            project.getService(OpenTelemetrySettingsStorageService::class.java)
    }
}
