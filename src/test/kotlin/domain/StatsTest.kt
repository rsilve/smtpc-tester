package domain

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class StatsTest {

    @Test
    internal fun `should send StatsReport`() = runTest {
        val response = CompletableDeferred<StatsReport>()
        val stats = Stats(1, 2)
        stats.sendStats( response)
        assertEquals(StatsReport(1, 2, total = 2), response.await())
    }

    @Test
    internal fun `should update stats with connect state`() {
        val stats = Stats()
        stats.updateStats(ConnectState)
        assertEquals(1, stats.connected)
    }

    @Test
    internal fun `should update stats with completed state`() {
        val stats = Stats(connected = 1)
        stats.updateStats(CompletedState)
        assertEquals(0, stats.connected)
    }

    @Test
    internal fun `should update stats with sendStatus sent state`() {
        val stats = Stats()
        stats.updateStats(SendStatusState)
        assertEquals(1, stats.sent)
    }

    @Test
    internal fun `should update stats with sendStatus not sent state`() {
        val stats = Stats()
        stats.updateStats(NotSendStatusState)
        assertEquals(0, stats.sent)
    }

    @Test
    internal fun `should update stats with unknown state`() {
        val stats = Stats()
        val origin = stats.copy()
        val state = object : SmtpSessionState {}
        stats.updateStats( state)
        assertEquals(stats, origin)
    }
}