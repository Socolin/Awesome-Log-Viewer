package fr.socolin.awesomeLogViewer.core.core.log_processor

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger.getInstance
import com.intellij.openapi.project.Project

val LOG = getInstance("LogProcessorManager")

@Service(Service.Level.PROJECT)
class LogProcessorManager(private val project: Project) {
    private val logProcessorDefinitions = mutableListOf<LogProcessorDefinition>()
    var preparedProcessors: List<LogProcessor>? = null

    fun getOrCreateLogProcessors(executionMode: ExecutionMode): List<LogProcessor> {
        val existingProcessors = preparedProcessors
        if (existingProcessors != null) {
            preparedProcessors = null
            return existingProcessors
        }
        return createLogProcessors(executionMode)
    }

    fun createLogProcessors(executionMode: ExecutionMode): List<LogProcessor> {
        val processors = mutableListOf<LogProcessor>()
        for (definition in logProcessorDefinitions) {
            try {
                val processor = definition.createProcessorIfActive(project, executionMode)
                if (processor != null) {
                    processors.add(processor)
                }
            } catch (e: Exception) {
                LOG.error("Failed to create log processor", e)
            }
        }
        return processors
    }

    fun registerLogProcessor(definition: LogProcessorDefinition) {
        logProcessorDefinitions.add(definition)
    }

    fun unregisterLogProcessor(id: String) {
        logProcessorDefinitions.removeIf { it.getId() == id }
    }

    fun storePreparedProcessors(newProcessors: List<LogProcessor>) {
        val processors = preparedProcessors
        if (processors != null) {
            for (processor in processors) {
                processor.dispose()
            }
        }
        preparedProcessors = newProcessors
    }

    companion object {
        fun getInstance(project: Project): LogProcessorManager = project.getService(LogProcessorManager::class.java)
    }
}

