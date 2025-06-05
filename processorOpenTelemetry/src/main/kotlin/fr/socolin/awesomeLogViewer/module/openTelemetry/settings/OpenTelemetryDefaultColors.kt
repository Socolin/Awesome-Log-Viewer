package fr.socolin.awesomeLogViewer.module.openTelemetry.settings

import com.intellij.ui.JBColor
import java.awt.Color

class OpenTelemetryDefaultColors {
    companion object {
        val metricColor: Color = JBColor.gray
        val traceColor: Color = JBColor.green
        val logRecordColor: Color = JBColor.blue
    }
}
