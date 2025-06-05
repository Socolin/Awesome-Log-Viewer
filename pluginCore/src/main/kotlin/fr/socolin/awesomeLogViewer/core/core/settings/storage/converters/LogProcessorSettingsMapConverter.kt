package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.ui.JBColor
import com.intellij.util.xmlb.Converter
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel

class MapOfColorPerSeverityConverter : Converter<MutableMap<GenericSeverityLevel, JBColor>>() {
    override fun fromString(value: String): MutableMap<GenericSeverityLevel, JBColor> {
        val result = mutableMapOf<GenericSeverityLevel, JBColor>()
        for (severityAndColor in value.split(",")) {
            val split = severityAndColor.split("=")
            val severity = GenericSeverityLevel.valueOf(split[0])
            val color = JBColor.decode(split[1])
            result[severity] = JBColor(color.rgb, color.rgb)
        }
        return result
    }

    override fun toString(value: MutableMap<GenericSeverityLevel, JBColor>): String {
        val sb = StringBuilder()
        for ((key, value) in value.entries) {
            if (key.defaultColor == value) {
                continue
            }
            sb.append(key).append('=').append(value).append(',')
        }
        if (sb.isNotEmpty()) {
            sb.setLength(sb.length - 1)
        }
        return sb.toString()
    }
}

