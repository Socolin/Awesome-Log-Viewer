package fr.socolin.awesomeLogViewer.core.core.settings.storage

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker
import com.intellij.openapi.util.SimpleModificationTracker
import com.jetbrains.rd.util.lifetime.LifetimeDefinition

abstract class BaseSettingsStorageService<T : BaseSettingsState>(
    private var _state: T
) :
    PersistentStateComponentWithModificationTracker<T>, Disposable {
    private val tracker = SimpleModificationTracker()
    private val lifetimeDefinition: LifetimeDefinition = LifetimeDefinition()

    override fun getStateModificationCount(): Long = tracker.modificationCount
    override fun getState(): T = _state

    override fun loadState(state: T) {
        this._state = state
        state.incrementTrackerOnChanges(lifetimeDefinition, tracker)
    }

    override fun noStateLoaded() {
        _state.incrementTrackerOnChanges(lifetimeDefinition, tracker)
    }

    override fun dispose() {
        lifetimeDefinition.terminate()
    }
}
