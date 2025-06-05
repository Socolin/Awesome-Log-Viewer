package fr.socolin.awesomeLogViewer.core.core.utilities

import com.intellij.ui.JBColor

enum class GenericSeverityLevel(val defaultColor: JBColor?) {
    Trace(JBColor.GRAY),
    Debug(JBColor.GRAY),
    Info(null),
    Warn(JBColor.ORANGE),
    Error(JBColor.RED),
    Critical(JBColor.PINK),
}
