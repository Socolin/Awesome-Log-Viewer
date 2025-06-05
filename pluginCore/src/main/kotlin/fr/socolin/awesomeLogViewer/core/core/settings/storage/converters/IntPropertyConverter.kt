package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.util.xmlb.Converter
import com.jetbrains.rd.util.reactive.Property

class IntPropertyConverter : Converter<Property<Int>>() {
    override fun fromString(value: String): Property<Int> {
        return Property(value.toIntOrNull() ?: 0)
    }

    override fun toString(value: Property<Int>): String {
        return value.value.toString()
    }
}
