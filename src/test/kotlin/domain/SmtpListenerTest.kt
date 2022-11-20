package domain

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import net.silve.smtpc.client.SendStatus
import net.silve.smtpc.client.SendStatusCode
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class SmtpListenerTest {

    @Test
    internal fun `should exists`() {
        SmtpListener { }
    }

    @Test
    internal fun `on Connect`() {
        var lastState: SmtpSessionState? = null
        val mockSend: (SmtpSessionState) -> Unit = { lastState = it }
        val listener = SmtpListener(mockSend)
        listener.onConnect("host", 25)
        assertTrue(lastState!! is ConnectState)
    }

    @Test
    internal fun `on SendStatus`() {
        var lastState: SmtpSessionState? = null
        val mockSend: (SmtpSessionState) -> Unit = { lastState = it }
        val listener = SmtpListener(mockSend)
        listener.onSendStatus("id", SendStatus(SendStatusCode.SENT, 250, listOf()))
        assertTrue(lastState!! is SendStatusState)
    }

    @Test
    internal fun `on Completed`() {
        var lastState: SmtpSessionState? = null
        val mockSend: (SmtpSessionState) -> Unit = { lastState = it }
        val listener = SmtpListener(mockSend)
        listener.onCompleted("id")
        assertTrue(lastState!! is CompletedState)
    }


    @Test
    internal fun `should have a factory`() {
        val notifier = mockk<(state: SmtpSessionState) -> Unit>()
        justRun { notifier(any()) }
        val listener = SmtpListener.create(notifier)
        listener.onCompleted("id")
        verify { notifier(ofType(CompletedState::class)) }

    }
}