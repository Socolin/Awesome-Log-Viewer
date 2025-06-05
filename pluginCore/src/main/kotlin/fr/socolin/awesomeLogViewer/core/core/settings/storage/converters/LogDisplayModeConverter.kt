package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.util.xmlb.Converter
import fr.socolin.awesomeLogViewer.core.core.session.LogDisplayMode
import com.jetbrains.rd.util.reactive.Property

class LogDisplayModeConverter : Converter<Property<LogDisplayMode>>() {
    override fun fromString(value: String): Property<LogDisplayMode> {
        return Property(LogDisplayMode.valueOf(value))
    }

    override fun toString(value: Property<LogDisplayMode>): String {
        return value.value.toString()
    }
}
