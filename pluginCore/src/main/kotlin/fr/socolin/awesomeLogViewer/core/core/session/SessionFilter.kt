package fr.socolin.awesomeLogViewer.core.core.session

import com.jetbrains.rd.util.AtomicInteger
import com.jetbrains.rd.util.getOrCreate
import com.jetbrains.rd.util.reactive.ISource
import com.jetbrains.rd.util.reactive.Signal
import java.awt.Color


data class FilterSectionDefinition(val sectionName: String, val displayName: String)
data class FilterValueDefinition(val sectionName: String, val value: String, val barColor: Color? = null, val foregroundColor: Color? = null)
data class FilterValue(val definition: FilterValueDefinition, val count: AtomicInteger = AtomicInteger(0))
class FilterSection(
    val sectionName: String,
    val displayName: String,
    val filterValues: MutableSet<FilterValue>
) {
    private val _valueAdded = Signal<FilterValue>()
    val valueAdded: ISource<FilterValue> get() = _valueAdded

    fun addValue(filterValueDefinition: FilterValueDefinition) {
        var filterValue = filterValues.find { filterValueDefinition.value == it.definition.value }
        if (filterValue == null) {
            filterValue = FilterValue(filterValueDefinition)
            filterValues.add(filterValue)
            _valueAdded.fire(filterValue)
        }
        filterValue.count.andIncrement
    }

    fun decrementValue(filterValueDefinition: FilterValueDefinition) {
        val filterValue = filterValues.find { filterValueDefinition.value == it.definition.value }
        filterValue?.count?.andDecrement
    }
}

class SessionFilter(
    var caseSensitiveFilter: Boolean,
    initialFilteredValues: Map<String, MutableSet<String>>
) {
    private val _filterSections = mutableMapOf<String, FilterSection>()
    val filterSections: Map<String, FilterSection> get() = _filterSections
    private val _sectionAdded = Signal<FilterSection>()
    val sectionAdded: ISource<FilterSection> get() = _sectionAdded

    private val _filteredValues = initialFilteredValues.toMutableMap()
    val filteredValues: Map<String, Set<String>> get() = _filteredValues
    var filterText: String? = null

    val filterChanged = Signal<SessionFilter>()

    fun addFilterSection(filterSectionDefinition: FilterSectionDefinition) {
        _filterSections[filterSectionDefinition.sectionName] = FilterSection(
            filterSectionDefinition.sectionName,
            filterSectionDefinition.displayName,
            mutableSetOf()
        )
    }

    fun addFilterValue(filterValue: FilterValueDefinition) {
        val section = _filterSections.getOrCreate(filterValue.sectionName) {
            val filterSection = FilterSection(filterValue.sectionName, filterValue.sectionName, mutableSetOf())
            _sectionAdded.fire(filterSection)
            filterSection
        }

        section.addValue(filterValue)
    }

    fun setFilteredValue(section: String, filterValue: FilterValue, filtered: Boolean) {
        if (filtered) {
            val values = _filteredValues.getOrCreate(section) { mutableSetOf() }
            if (values.add(filterValue.definition.value))
                filterChanged.fire(this)
        } else {
            val values = _filteredValues[section]
            if (values?.remove(filterValue.definition.value) == true)
                filterChanged.fire(this)
        }
    }

    fun updateTextFilterValue(filter: String) {
        filterText = filter
        filterChanged.fire(this)
    }

    fun setCaseSensitive(caseSensitive: Boolean) {
        caseSensitiveFilter = caseSensitive
        filterChanged.fire(this)
    }

    fun updateFilterValuesWithNewLog(logEntry: LogEntry) {
        for (filterValue in logEntry.getFilteringParameters()) {
            addFilterValue(filterValue)
        }
    }

    fun decrementCount(filterValue: FilterValueDefinition) {
        val filterSection = filterSections.getValue(filterValue.sectionName)
        filterSection.decrementValue(filterValue)
    }

    fun resetCounter() {
        for (filterSection in filterSections.values) {
            filterSection.filterValues.forEach { it.count.set(0) }
        }
    }
}
