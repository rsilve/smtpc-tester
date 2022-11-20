package domain

import net.silve.smtpc.message.MessageFactory
import net.silve.smtpc.message.SmtpSession

object SessionGenerator {

    fun get(parameters: Parameters, factory: MessageFactory): SmtpSession {
        val session: SmtpSession = SmtpSession.newInstance(parameters.host, parameters.port)
        session.setMessageFactory(factory)
        return session
    }
}