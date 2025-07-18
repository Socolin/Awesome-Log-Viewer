package fr.socolin.awesomeLogViewer.platformSpecificRider.session.debug

import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebuggerManagerListener
import com.jetbrains.rider.debugger.DotNetDebugProcess
import fr.socolin.awesomeLogViewer.core.core.log_processor.ConsoleLogProcessor
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import fr.socolin.awesomeLogViewer.core.core.session.debug.DebugLogSession
import fr.socolin.awesomeLogViewer.core.core.session.run.ProcessHandlerOutputLineUtil

class MyDebuggerManagerListener(
    private val project: Project
) : XDebuggerManagerListener {
    override fun processStarted(debugProcess: XDebugProcess) {
        if (debugProcess !is DotNetDebugProcess)
            return

        val logProcessorManager = LogProcessorManager.getInstance(project)
        for (logProcessor in logProcessorManager.getOrCreateLogProcessors(ExecutionMode.DEBUG)) {
            val session = DebugLogSession(logProcessor, project) { debugProcess.session.ui }
            if (logProcessor is NetworkLogProcessor) {
                // FIXME: cannot access env variables here, but probably ok since it's done in MyPatchCommandLineExtension
                logProcessor.startNetworkCollector(emptyMap())
                logProcessor.logReceived.advise(session.lifetime, session::addLogLine)
            }
            if (logProcessor is ConsoleLogProcessor) {
                if (logProcessor.shouldListenToDebugOutput()) {
                    debugProcess.sessionProxy.targetDebug.advise(debugProcess.sessionLifetime) { outputMessage ->
                        val logEntry = logProcessor.processLogLine(outputMessage.output)
                        if (logEntry != null) {
                            session.addLogLine(logEntry)
                        }
                    }
                }
                if (logProcessor.shouldListenToConsoleOutput()) {
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
