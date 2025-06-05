package fr.socolin.awesomeLogViewer.core.core.tool_window.filter

import com.intellij.icons.AllIcons
import com.intellij.ide.ui.laf.darcula.DarculaUIUtil
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Separator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.BaseButtonBehavior
import com.intellij.util.ui.JBUI
import fr.socolin.awesomeLogViewer.core.core.CoreBundle
import fr.socolin.awesomeLogViewer.core.core.session.FilterValue
import com.jetbrains.rd.util.reactive.Signal
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.border.Border

data class FilterValueToggled(val value: FilterValue, val filtered: Boolean)

class FilterComponent(labelText: String, initialValues: Set<FilterValue>, initialFilteredValues: Set<String>?) : JBPanel<FilterComponent>() {
    val filterValues: MutableList<FilterValue> = mutableListOf(*initialValues.toTypedArray())
    val filteredValues: MutableSet<String> = initialFilteredValues?.toMutableSet() ?: mutableSetOf()
    val onFilterChanged = Signal<FilterValueToggled>()

    init {
        isOpaque = false
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        isFocusable = true
        border = SimpleFocusBorder()

        val label = JBLabel(labelText)
        toolTipText = CoreBundle.Companion.message("misc.filter.component.tooltip.message")
        add(label)
        add(JBLabel(AllIcons.General.ArrowDown))

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                openFilterSelector(label)
            }
        })

        object : BaseButtonBehavior(this, null as Void?) {
            override fun execute(e: MouseEvent) {
                openFilterSelector(label)
            }
        }.also { it.setupListeners() }
    }

    fun addValue(filterValue: FilterValue, filtered: Boolean) {
        filterValues.add(filterValue)
        if (filtered)
            filteredValues.add(filterValue.definition.value)
    }

    private fun openFilterSelector(label: JBLabel) {
        val actionGroup = DefaultActionGroup()

        val filterActions: MutableList<FilterSelectorToolbarAction> = mutableListOf()
        for (filterValue in filterValues.sortedBy { it.toString() }) {
            val filterAction = FilterSelectorToolbarAction(
                filterValue.definition.value + " - " + filterValue.count,
                !filteredValues.contains(filterValue.definition.value),
            ) {
                onFilterChanged.fire(FilterValueToggled(filterValue, !it))
                if (it) {
                    filteredValues.remove(filterValue.definition.value)
                } else {
                    filteredValues.add(filterValue.definition.value)
                }
            }
            filterActions.add(filterAction)
            actionGroup.add(filterAction)
        }
        actionGroup.add(Separator())
        actionGroup.add(SimpleAction(CoreBundle.Companion.message("misc.filter.component.select.all")) {
            for (filterValue in filterValues) {
                if (filteredValues.remove(filterValue.definition.value))
                    onFilterChanged.fire(FilterValueToggled(filterValue, false))
            }
        })
        actionGroup.add(SimpleAction(CoreBundle.Companion.message("misc.filter.component.deselect.all")) {
            for (filterValue in filterValues) {
                if (filteredValues.add(filterValue.definition.value))
                    onFilterChanged.fire(FilterValueToggled(filterValue, true))
            }
        })
        ActionManager.getInstance().createActionPopupMenu("AwesomeLogViewer_OpenFilter", actionGroup)
            .component
            .show(label, 0, label.height)
    }
}

internal class SimpleFocusBorder : Border {
    override fun paintBorder(c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int) {
        if (c?.hasFocus() == true && g is Graphics2D) {
            DarculaUIUtil.paintFocusBorder(g, width, height, 0f, true)
        }
    }

    override fun getBorderInsets(c: Component): Insets {
        val bw = DarculaUIUtil.BW.float
        val lw = DarculaUIUtil.LW.float
        val insets = (bw + lw).toInt()
        return JBUI.insets(insets, insets, insets, insets)
    }

    override fun isBorderOpaque() = false
}
