package domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.test.runTest
import net.silve.smtpc.message.Message
import net.silve.smtpc.message.MessageFactory
import net.silve.smtpc.message.SmtpSession
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SessionProducerKtTest {

    @Test
    fun `should produce 1 session from 1 message`() = runTest {
        val parameters = Parameters()
        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        val generateSession: (MessageFactory) -> SmtpSession = { factory -> SessionGenerator.get(parameters, factory) }
        val sessions = produceSessions(1, 1, generateMessage, generateSession)
        val session = sessions.consumeAsFlow().count()
        assertEquals(1, session)
    }

    @Test
    fun `should produce 1 session from 2 messages`() = runTest {
        val parameters = Parameters()
        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        val generateSession: (MessageFactory) -> SmtpSession = { factory -> SessionGenerator.get(parameters, factory) }
        val sessions = produceSessions(2, 2, generateMessage, generateSession)
        val session = sessions.consumeAsFlow().count()
        assertEquals(1, session)
    }

    @Test
    fun `should produce 2 sessions from 4 messages`() = runTest {
        val parameters = Parameters()
        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        val generateSession: (MessageFactory) -> SmtpSession = { factory -> SessionGenerator.get(parameters, factory) }
        val sessions = produceSessions(4, 2, generateMessage, generateSession)
        val session = sessions.consumeAsFlow().count()
        assertEquals(2, session)
    }

    @Test
    fun `should produce 2 sessions from 3 messages`() = runTest {
        val parameters = Parameters()
        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        val generateSession: (MessageFactory) -> SmtpSession = { factory -> SessionGenerator.get(parameters, factory) }
        val sessions = produceSessions(3, 2, generateMessage, generateSession)
        val session = sessions.consumeAsFlow().count()
        assertEquals(2, session)
    }
}