package fr.socolin.awesomeLogViewer.module.applicationInsights.settings.storage

import com.intellij.util.xmlb.annotations.OptionTag
import fr.socolin.awesomeLogViewer.core.core.settings.storage.BaseSettingsState
import fr.socolin.awesomeLogViewer.module.applicationInsights.settings.ApplicationInsightsDefaultColors
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.ColorPropertyConverter
import com.jetbrains.rd.util.reactive.Property
import java.awt.Color

class GlobalApplicationInsightsSettingsState : BaseSettingsState() {
    @OptionTag("METRIC_COLOR", converter = ColorPropertyConverter::class)
    val metricColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.metricColor)

    @OptionTag("EXCEPTION_COLOR", converter = ColorPropertyConverter::class)
    val exceptionColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.exceptionColor)

    @OptionTag("MESSAGE_COLOR", converter = ColorPropertyConverter::class)
    val messageColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.messageColor)

    @OptionTag("DEPENDENCY_COLOR", converter = ColorPropertyConverter::class)
    val dependencyColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.dependencyColor)

    @OptionTag("REQUEST_COLOR", converter = ColorPropertyConverter::class)
    val requestColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.requestColor)

    @OptionTag("EVENT_COLOR", converter = ColorPropertyConverter::class)
    val eventColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.eventColor)

    @OptionTag("PAGE_VIEW_COLOR", converter = ColorPropertyConverter::class)
    val pageViewColor = Property<Color>(ApplicationInsightsDefaultColors.Companion.pageViewColor)

    override fun registerProperties() {
        incrementTrackerWhenPropertyChanges(metricColor)
        incrementTrackerWhenPropertyChanges(exceptionColor)
        incrementTrackerWhenPropertyChanges(messageColor)
        incrementTrackerWhenPropertyChanges(dependencyColor)
        incrementTrackerWhenPropertyChanges(requestColor)
        incrementTrackerWhenPropertyChanges(eventColor)
        incrementTrackerWhenPropertyChanges(pageViewColor)
    }
}
