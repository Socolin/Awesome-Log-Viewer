package fr.socolin.awesomeLogViewer.module.applicationInsights.settings

import com.intellij.ui.JBColor
import java.awt.Color

class ApplicationInsightsDefaultColors {
    companion object {
        val metricColor: Color = JBColor.gray
        val exceptionColor: Color = JBColor.red
        val messageColor: Color = JBColor.orange
        val dependencyColor: Color = JBColor.blue
        val requestColor: Color = JBColor.green
        val eventColor: Color = JBColor.cyan
        val pageViewColor: Color = JBColor.yellow
    }
}
