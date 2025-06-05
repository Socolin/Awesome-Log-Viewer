package fr.socolin.awesomeLogViewer.module.simpleConsole.settings.storage.converters

import com.intellij.util.xmlb.Converter
import fr.socolin.awesomeLogViewer.module.simpleConsole.LogParsingMethod
import com.jetbrains.rd.util.reactive.Property

class LogParsingMethodPropertyConverter : Converter<Property<LogParsingMethod>>() {
    override fun fromString(value: String): Property<LogParsingMethod>? {
        return Property(LogParsingMethod.valueOf(value))
    }

    override fun toString(value: Property<LogParsingMethod>): String? {
        return value.value.toString()
    }
}
