package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.util.xmlb.Converter
import com.jetbrains.rd.util.reactive.Property

class StringPropertyConverter : Converter<Property<String>>() {
    override fun fromString(value: String): Property<String>? {
        return Property(value)
    }

    override fun toString(value: Property<String>): String? {
        return value.value
    }
}
