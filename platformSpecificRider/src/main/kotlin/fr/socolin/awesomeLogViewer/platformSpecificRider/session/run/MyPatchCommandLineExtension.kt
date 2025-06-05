package fr.socolin.awesomeLogViewer.platformSpecificRider.session.run

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessInfo
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.project.Project
import fr.socolin.awesomeLogViewer.core.core.log_processor.ExecutionMode
import fr.socolin.awesomeLogViewer.core.core.log_processor.LogProcessorManager
import fr.socolin.awesomeLogViewer.core.core.log_processor.NetworkLogProcessor
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rider.run.PatchCommandLineExtension
import com.jetbrains.rider.run.WorkerRunInfo
import com.jetbrains.rider.runtime.DotNetRuntime
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise

class MyPatchCommandLineExtension(
    val project: Project,
) : PatchCommandLineExtension {
    override fun patchDebugCommandLine(
        lifetime: Lifetime,
        workerRunInfo: WorkerRunInfo,
        processInfo: ProcessInfo?,
        project: Project
    ): Promise<WorkerRunInfo> {
        val logProcessorManager = LogProcessorManager.getInstance(project)
        val logProcessors = logProcessorManager.createLogProcessors(ExecutionMode.DEBUG)
        for (logProcessor in logProcessors) {
            if (logProcessor is NetworkLogProcessor) {
                logProcessor.startNetworkCollector()
            }
            workerRunInfo.commandLine.withEnvironment(logProcessor.getEnvironmentVariables())
        }
        logProcessorManager.storePreparedProcessors(logProcessors)
        return resolvedPromise(workerRunInfo)
    }

    override fun patchRunCommandLine(
        commandLine: GeneralCommandLine,
        dotNetRuntime: DotNetRuntime,
        project: Project
    ): ProcessListener? {
        val logProcessorManager = LogProcessorManager.getInstance(project)
        val logProcessors = logProcessorManager.createLogProcessors(ExecutionMode.RUN)
        for (logProcessor in logProcessors) {
            if (logProcessor is NetworkLogProcessor) {
                logProcessor.startNetworkCollector()
            }
            commandLine.withEnvironment(logProcessor.getEnvironmentVariables())
        }
        logProcessorManager.storePreparedProcessors(logProcessors)
        return null
    }
}
