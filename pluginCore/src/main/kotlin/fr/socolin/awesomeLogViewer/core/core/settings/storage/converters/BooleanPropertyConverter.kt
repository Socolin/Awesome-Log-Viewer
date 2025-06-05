package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.util.xmlb.Converter
import com.jetbrains.rd.util.reactive.Property

class BooleanPropertyConverter : Converter<Property<Boolean>>() {
    override fun fromString(value: String): Property<Boolean> {
        return Property(value == "true")
    }

    override fun toString(value: Property<Boolean>): String {
        return value.value.toString()
    }
}

