package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.util.xmlb.Converter
import com.jetbrains.rd.util.reactive.Property

class FloatPropertyConverter : Converter<Property<Float>>() {
    override fun fromString(value: String): Property<Float> {
        return Property(value.toFloatOrNull() ?: 0.0f)
    }

    override fun toString(value: Property<Float>): String {
        return value.value.toString()
    }
}
