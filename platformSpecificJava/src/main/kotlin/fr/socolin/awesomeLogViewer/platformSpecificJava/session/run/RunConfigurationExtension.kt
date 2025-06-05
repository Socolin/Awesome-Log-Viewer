package fr.socolin.awesomeLogViewer.platformSpecificJava.session.run

import com.intellij.execution.RunConfigurationExtension
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.openapi.diagnostic.logger
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor

private val LOG = logger<MyRunConfigurationExtension>()

class MyRunConfigurationExtension : RunConfigurationExtension() {
    override fun <T : RunConfigurationBase<*>?> updateJavaParameters(
        configuration: T & Any,
        params: JavaParameters,
        runnerSettings: RunnerSettings?
    ) {
        try {
            val logProcessorManager = LogProcessorManager.getInstance(configuration.project)
            val logProcessors = logProcessorManager.createLogProcessors(ExecutionMode.DEBUG)
            for (logProcessor in logProcessors) {
                if (logProcessor is NetworkLogProcessor) {
                    logProcessor.startNetworkCollector()
                }
                params.env.putAll(logProcessor.getEnvironmentVariables())
            }
            logProcessorManager.storePreparedProcessors(logProcessors)
        } catch (e: Throwable) {
            LOG.warn("Failed to update Environment variable and Java parameters", e)
        }
    }

    override fun isApplicableFor(configuration: RunConfigurationBase<*>): Boolean {

        return true
    }
}
