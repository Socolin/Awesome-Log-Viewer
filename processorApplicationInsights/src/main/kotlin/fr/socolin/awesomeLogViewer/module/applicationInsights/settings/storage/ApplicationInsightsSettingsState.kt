package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage

import com.intellij.util.xmlb.annotations.MapAnnotation
import com.intellij.util.xmlb.annotations.Tag
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.ConsoleLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.log_processor.shared.settings.storage.NetworkLogProcessorSettingsState
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsState

class ApplicationInsightsSettingsState : BaseSettingsState() {
    @Tag("consoleSettings")
    val consoleSettings = ApplicationInsightsConsoleSettingsState()
    @Tag("networkSettings")
    val networkSettings = ApplicationInsightsNetworkSettingsState()

    override fun registerProperties() {
        registerChildState(consoleSettings)
        registerChildState(networkSettings)
    }
}

@Tag("applicationInsightsConsoleSettings")
class ApplicationInsightsConsoleSettingsState : ConsoleLogProcessorSettingsState() {
    init {
        enableForRun.set(false)
        readConsoleOutput.set(false)
    }

    override fun registerProperties() {
        super.registerProperties()
    }
}

@Tag("applicationInsightsNetworkSettings")
class ApplicationInsightsNetworkSettingsState : NetworkLogProcessorSettingsState() {
    init {
        enableForRun.set(true)
        enableForDebug.set(false)
    }

    @MapAnnotation(surroundWithTag = false, surroundKeyWithTag = false, surroundValueWithTag = false)
    val environmentVariables = mutableMapOf(
        Pair(
            "APPLICATIONINSIGHTS_CONNECTION_STRING",
            "InstrumentationKey=12345678-0000-0000-0000-009876543210;IngestionEndpoint=http://localhost:\${SERVER_PORT}/"
        ),
        Pair(
            "APPINSIGHTS_DEVELOPER_MODE",
            "true"
        )
    )

    override fun registerProperties() {
        super.registerProperties()
    }
}
