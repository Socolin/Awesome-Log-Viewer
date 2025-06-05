package fr.socolin.awesomeLogViewer.core.core.session.run

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor


class ProgramExecutionListener(
    private val project: Project
) : ExecutionListener {
    override fun processStarted(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler
    ) {
        val isRider = ApplicationInfo.getInstance().build.productCode == "RD"
        if (isRider) {
            return
        }

        val processHandlerOutputLineUtil = ProcessHandlerOutputLineUtil(handler)
        val logProcessorManager = LogProcessorManager.Companion.getInstance(project)
        val logProcessors = logProcessorManager.getOrCreateLogProcessors(ExecutionMode.RUN)
        for (logProcessor in logProcessors) {
            val session = OutputLogSession(logProcessor, env)
            if (logProcessor is NetworkLogProcessor) {
                logProcessor.startNetworkCollector()
                logProcessor.logReceived.advise(session.lifetime, session::addLogLine)
            }
            if (logProcessor is ConsoleLogProcessor) {
                processHandlerOutputLineUtil.lineReceived.advise(session.lifetime) {
                    val logEntry = logProcessor.processLogLine(it.line)
                    if (logEntry != null) {
                        session.addLogLine(logEntry)
                    }
                }
            }
        }
        processHandlerOutputLineUtil.startListening()
    }
}
