package fr.socolin.awesomeLogViewer.core.core.settings.storage

import com.intellij.openapi.util.SimpleModificationTracker
import com.jetbrains.rd.util.Maybe
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.Property
import com.jetbrains.rd.util.reactive.adviseWithPrev

abstract class BaseSettingsState {
    private lateinit var tracker: SimpleModificationTracker
    private lateinit var lifetime: Lifetime

    fun incrementTrackerOnChanges(lifetime: Lifetime, tracker: SimpleModificationTracker) {
        this.tracker = tracker
        this.lifetime = lifetime
        registerProperties()
    }

    protected fun <T> incrementTrackerWhenPropertyChanges(property: Property<T>) {
        property.adviseWithPrev(lifetime) { previous, newValue ->
            if (previous != Maybe.None) {
                if (previous.asNullable != newValue) {
                    tracker.incModificationCount()
                }
            }
        }
    }

    fun registerChildState(childState: BaseSettingsState) {
        childState.incrementTrackerOnChanges(lifetime, tracker)
    }

    protected fun incModificationCount() {
        tracker.incModificationCount()
    }

    protected abstract fun registerProperties()
}
