package fr.socolin.awesomeLogViewer.core.core.session.debug

import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebuggerManagerListener
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.run.ProcessHandlerOutputLineUtil

class DebuggerManagerListener(
    private val project: Project
) : XDebuggerManagerListener {
    override fun processStarted(debugProcess: XDebugProcess) {
        val isRider = ApplicationInfo.getInstance().build.productCode == "RD"
        if (isRider) {
            return
        }

        val logProcessorManager = LogProcessorManager.Companion.getInstance(project)
        for (logProcessor in logProcessorManager.getOrCreateLogProcessors(ExecutionMode.DEBUG)) {
            val session = DebugLogSession(logProcessor, project) { debugProcess.session.ui }
            if (logProcessor is NetworkLogProcessor) {
                // FIXME: We cannot access environment variable here
                logProcessor.startNetworkCollector(emptyMap())
                logProcessor.logReceived.advise(session.lifetime, session::addLogLine)
            }
            if (logProcessor is ConsoleLogProcessor) {
                if (logProcessor.shouldListenToConsoleOutput() || !DebugOutputHelper.isDebugOutputSupported()) {
                    val processHandlerOutputLineUtil = ProcessHandlerOutputLineUtil(debugProcess.processHandler)
                    processHandlerOutputLineUtil.lineReceived.advise(session.lifetime) {
                        val logEntry = logProcessor.processLogLine(it.line)
                        if (logEntry != null) {
                            session.addLogLine(logEntry)
                        }
                    }
                    processHandlerOutputLineUtil.startListening()
                }
            }
        }
    }
}
