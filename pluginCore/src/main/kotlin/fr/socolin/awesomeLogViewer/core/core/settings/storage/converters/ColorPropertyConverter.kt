package fr.socolin.awesomeLogViewer.core.core.settings.storage.converters

import com.intellij.util.xmlb.Converter
import com.jetbrains.rd.util.reactive.Property
import java.awt.Color

class ColorPropertyConverter : Converter<Property<Color>>() {
    override fun fromString(value: String): Property<Color>? {
        return Property(Color.decode(value))
    }

    override fun toString(value: Property<Color>): String? {
        return String.format("#%02x%02x%02x", value.value.red, value.value.green, value.value.blue);
    }
}
