package fr.socolin.awesomeLogViewer.core.core.session.run

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.util.Key
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rd.util.reactive.ISource
import com.jetbrains.rd.util.reactive.Signal

data class ProcessOutputLine(val line: String, val outputType: Key<*>)

class ProcessHandlerOutputLineUtil(
    private val processHandler: ProcessHandler
) {
    val outputBuffers = mutableMapOf<Key<*>, StringBuilder>()
    private val _lineReceived = Signal<ProcessOutputLine>()
    val lineReceived: ISource<ProcessOutputLine>
        get() = _lineReceived

    fun startListening() {
        processHandler.addProcessListener(object : ProcessListener {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                val buffer = outputBuffers.getOrCreate(outputType) { java.lang.StringBuilder() }
                var lastNewLineIndex = 0
                while (true) {
                    val newLineIndex = event.text.indexOf('\n', lastNewLineIndex)
                    if (newLineIndex == -1) {
                        buffer.append(event.text.substring(lastNewLineIndex))
                        break
                    } else {
                        buffer.append(event.text.substring(lastNewLineIndex, newLineIndex))
                    }
                    _lineReceived.fire(ProcessOutputLine(buffer.toString(), outputType))
                    buffer.clear()
                    lastNewLineIndex = newLineIndex + 1
                }
            }
        })
    }
}
