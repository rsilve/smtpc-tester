package application

import domain.MessageGenerator
import domain.Parameters
import domain.SessionGenerator
import domain.produceSessions
import infra.SmtpRunner
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import net.silve.smtpc.message.Message
import net.silve.smtpc.message.MessageFactory
import net.silve.smtpc.message.SmtpSession

class TestPlanSimple(
    parameters: Parameters,
    statsService: StatsService
) : TestPlanAbstract(parameters, statsService) {

    private val smtpRunner = SmtpRunner()

    override suspend fun execute() = coroutineScope {
        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        val generateSession: (MessageFactory) -> SmtpSession = { factory -> SessionGenerator.get(parameters, factory) }
        val producer = produceSessions(parameters.messagesNumber, 1, generateMessage, generateSession)

        producer.consumeAsFlow()
            .collect { session ->
                session.listener = listener
                smtpRunner.run(session)
            }
    }

    override suspend fun onComplete() {
        super.onComplete()
        smtpRunner.stop()
    }
}