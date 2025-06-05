package fr.socolin.awesomeLogViewer.core.core.settings.storage

import com.intellij.ui.JBColor
import com.intellij.util.xmlb.annotations.OptionTag
import com.intellij.util.xmlb.annotations.XMap
import fr.socolin.awesomeLogViewer.core.core.session.LogDisplayMode
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.BooleanPropertyConverter
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.FloatPropertyConverter
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.IntPropertyConverter
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.LogDisplayModeConverter
import fr.socolin.awesomeLogViewer.core.core.settings.storage.converters.MapOfColorPerSeverityConverter
import fr.socolin.awesomeLogViewer.core.core.settings.ui.GlobalPluginSettingsViewModel
import fr.socolin.awesomeLogViewer.core.core.utilities.GenericSeverityLevel
import com.jetbrains.rd.util.reactive.Property
import java.awt.Color
import kotlin.collections.iterator
import kotlin.collections.set
import kotlin.text.startsWith

class GlobalPluginSettingsState : BaseSettingsState() {
    @OptionTag("SHOW_SAMPLED_INDICATOR", converter = BooleanPropertyConverter::class)
    val showSampledIndicator: Property<Boolean> = Property(false)

    @OptionTag("USE_SOFT_WRAP", converter = BooleanPropertyConverter::class)
    val useSoftWrap: Property<Boolean> = Property(false)

    @OptionTag("IS_FILTER_CASE_SENSITIVE", converter = BooleanPropertyConverter::class)
    val isFilterCaseSensitive: Property<Boolean> = Property(false)

    @OptionTag("SCROLL_TO_END", converter = BooleanPropertyConverter::class)
    val scrollToEnd: Property<Boolean> = Property(false)

    @OptionTag("AUTOMATICALLY_EXPAND_NEW_LOG", converter = BooleanPropertyConverter::class)
    val automaticallyExpandNewLog: Property<Boolean> = Property(true)

    @OptionTag("MAX_LOG_COUNT", converter = IntPropertyConverter::class)
    val maxLogCount: Property<Int> = Property(10_000)

    @OptionTag("LOG_LIST_SPLIT_PROPORTION", converter = FloatPropertyConverter::class)
    val logListSplitProportion: Property<Float> = Property(0.6f)

    @OptionTag("COLOR_LOG_BASED_ON_SEVERITY", converter = BooleanPropertyConverter::class)
    val showTimeFromStart: Property<Boolean> = Property(false)

    @OptionTag("COLOR_LOG_BASED_ON_SEVERITY", converter = BooleanPropertyConverter::class)
    val colorLogBasedOnSeverity: Property<Boolean> = Property(true)

    @OptionTag("LOG_DISPLAY_MODE", converter = LogDisplayModeConverter::class)
    val logDisplayMode: Property<LogDisplayMode> = Property(LogDisplayMode.Flat)

    @XMap(propertyElementName = "filteredValues")
    val filteredValuesBySectionAndProcessor: MutableMap<String, String> = mutableMapOf()

    @XMap(propertyElementName = "hiddenColumnsByProcessor")
    val hiddenColumnsByProcessor: MutableMap<String, String> = mutableMapOf()

    @OptionTag("COLOR_PER_SEVERITY", converter = MapOfColorPerSeverityConverter::class)
    val colorPerSeverity = mutableMapOf<GenericSeverityLevel, JBColor?>().apply {
        putAll(GenericSeverityLevel.entries.associateWith { it.defaultColor })
    }

    @OptionTag("SHOW_SAMPLED_INDICATOR", converter = BooleanPropertyConverter::class)
    val createPendingParent: Property<Boolean> = Property(true)

    fun areSettingsEquals(settingModel: GlobalPluginSettingsViewModel): Boolean {
        return settingModel.maxLogCount == maxLogCount.value
            && settingModel.showTimeFromStart == showTimeFromStart.value
            && settingModel.createPendingParent == createPendingParent.value
            && settingModel.colorLogBasedOnSeverity == colorLogBasedOnSeverity.value
            && areSeverityColorsEquals(colorPerSeverity, settingModel.colorPerSeverity)
    }

    private fun areSeverityColorsEquals(
        colorPerSeverity: MutableMap<GenericSeverityLevel, JBColor?>,
        colorPerSeverity2: MutableMap<GenericSeverityLevel, Color?>
    ): Boolean {
        if (colorPerSeverity.size != colorPerSeverity2.size) {
            return false
        }

        for ((severity, color) in colorPerSeverity) {
            if (color != colorPerSeverity2[severity]) {
                return false
            }
        }

        return true
    }

    override fun registerProperties() {
        incrementTrackerWhenPropertyChanges(showSampledIndicator)
        incrementTrackerWhenPropertyChanges(useSoftWrap)
        incrementTrackerWhenPropertyChanges(isFilterCaseSensitive)
        incrementTrackerWhenPropertyChanges(scrollToEnd)
        incrementTrackerWhenPropertyChanges(maxLogCount)
        incrementTrackerWhenPropertyChanges(logListSplitProportion)
        incrementTrackerWhenPropertyChanges(colorLogBasedOnSeverity)
        incrementTrackerWhenPropertyChanges(showTimeFromStart)
        incrementTrackerWhenPropertyChanges(createPendingParent)
    }

    fun updateFilteredValues(logProcessorId: String, filteredValuesBySection: Map<String, Set<String>>) {
        val prefix = "$logProcessorId."
        val keysToRemove = filteredValuesBySectionAndProcessor.keys.filter { it.startsWith(prefix) }.toMutableSet()
        for (key in keysToRemove) {
            filteredValuesBySectionAndProcessor.remove(key)
        }
        for ((sectionName, values) in filteredValuesBySection.entries) {
            filteredValuesBySectionAndProcessor[prefix + sectionName] = values.joinToString(";")
        }
        incModificationCount()
    }

    fun hideColumn(logProcessorId: String, columnId: String) {
        val hiddenColumns = getHiddenColumns(logProcessorId).toMutableSet()
        hiddenColumns.add(columnId)
        updateHiddenColumns(logProcessorId, hiddenColumns)
    }

    fun showColumn(logProcessorId: String, columnId: String) {
        val hiddenColumns = getHiddenColumns(logProcessorId).toMutableSet()
        hiddenColumns.remove(columnId)
        updateHiddenColumns(logProcessorId, hiddenColumns)
    }

    fun updateHiddenColumns(logProcessorId: String, hiddenColumns: Set<String>) {
        hiddenColumnsByProcessor[logProcessorId] = hiddenColumns.joinToString(";")
        incModificationCount()
    }

    fun getHiddenColumns(logProcessorId: String): Set<String> {
        return hiddenColumnsByProcessor[logProcessorId]?.split(";")?.toSet() ?: emptySet()
    }
}
