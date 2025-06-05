package fr.socolin.awesomeLogViewer.core.session

import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.LogSortState
import fr.socolin.awesomeLogViewer.core.core.session.visibleLog.VisibleLogChangeNotifier
import fr.socolin.awesomeLogViewer.core.core.settings.storage.GlobalPluginSettingsStorageService
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import fr.socolin.awesomeLogViewer.core.core.session.FlatLogContainer
import fr.socolin.awesomeLogViewer.core.core.session.LogEntry
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.core.core.session.SessionFilter
import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import kotlin.test.Test

class LogContainerTest {
    lateinit var lifetimeDefinition: LifetimeDefinition
    lateinit var logContainer: FlatLogContainer

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        lifetimeDefinition = LifetimeDefinition()
        val sessionFilter = SessionFilter(true, mapOf())
        logContainer = FlatLogContainer(
            lifetimeDefinition.lifetime,
            sessionFilter,
            GlobalPluginSettingsStorageService(),
            VisibleLogChangeNotifier(),
            LogSortState(),
        )
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        lifetimeDefinition.terminate()
    }

    @Test
    fun addLogEntry_WhenAddingParentThenChild_HierarchyIsCorrect() {
        val parenLog = createLogEntry("1")
        val childLog = createLogEntry("2", "1");
        logContainer.addLogEntry(parenLog)
        logContainer.addLogEntry(childLog)

        assertEquals(parenLog, logContainer.getVisibleLogAt(0)?.logEntry)
        assertEquals(childLog, logContainer.getVisibleLogAt(1)?.logEntry)
    }

    fun createLogEntry(
        id: String? = null,
        parentId: String? = null
    ): LogEntry {
        val timeInfo = LogEntryTimeInfo.createFromStartAndDuration(Instant.ofEpochMilli(1), java.time.Duration.ZERO)
        return FakeLogEntry(timeInfo, id, parentId)
    }
}


