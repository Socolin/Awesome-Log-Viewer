package fr.socolin.awesomeLogViewer.module.openTelemetry.settings.storage

import com.intellij.util.xmlb.annotations.OptionTag
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsState
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.ColorPropertyConverter
import fr.socolin.awesomeLogViewer.module.openTelemetry.settings.OpenTelemetryDefaultColors
import com.jetbrains.rd.util.reactive.Property

class GlobalOpenTelemetrySettingsState : BaseSettingsState() {
    @OptionTag("METRIC_COLOR", converter = ColorPropertyConverter::class)
    val metricColor = Property(OpenTelemetryDefaultColors.Companion.metricColor)

    @OptionTag("TRACE_COLOR", converter = ColorPropertyConverter::class)
    val traceColor = Property(OpenTelemetryDefaultColors.Companion.traceColor)

    @OptionTag("LOG_COLOR", converter = ColorPropertyConverter::class)
    val logRecordColor = Property(OpenTelemetryDefaultColors.Companion.logRecordColor)

    override fun registerProperties() {
        incrementTrackerWhenPropertyChanges(metricColor)
        incrementTrackerWhenPropertyChanges(traceColor)
        incrementTrackerWhenPropertyChanges(logRecordColor)
    }
}
