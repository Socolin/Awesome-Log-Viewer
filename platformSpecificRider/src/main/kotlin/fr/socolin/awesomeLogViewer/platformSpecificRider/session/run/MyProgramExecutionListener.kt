package fr.socolin.awesomeLogViewer.platformSpecificRider.session.run

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.run.OutputLogSession
import fr.socolin.awesomeLogViewer.core.core.session.run.ProcessHandlerOutputLineUtil
import com.jetbrains.rider.debugger.DebuggerWorkerProcessHandler


class MyProgramExecutionListener(
    private val project: Project
) : ExecutionListener {
    override fun processStarted(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler
    ) {
        // This is handled in the DebuggerManagerListener
        if (handler is DebuggerWorkerProcessHandler)
            return

        val processHandlerOutputLineUtil = ProcessHandlerOutputLineUtil(handler)
        val logProcessorManager = LogProcessorManager.getInstance(project)
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
