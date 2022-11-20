package application

import net.silve.domain.*
import net.silve.infra.SmtpRunner
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import net.silve.smtpc.message.Message
import net.silve.smtpc.message.MessageFactory
import net.silve.smtpc.message.SmtpSession

class TestPlanConcurrent(
    parameters: Parameters,
    statsService: StatsService
) : TestPlanAbstract(parameters, statsService) {

    private val smtpRunner = SmtpRunner()

    override suspend fun execute() = coroutineScope {

        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        val generateSession: (MessageFactory) -> SmtpSession = { factory -> SessionGenerator.get(parameters, factory) }
        val sessionProducer = parameters.run {
            produceSessions(messagesNumber, batchSize, generateMessage, generateSession)
        }
        repeat(parameters.users) {
            launch {
                oneSender(sessionProducer, pool, smtpRunner)
            }
        }
        return@coroutineScope
    }

    private suspend fun oneSender(
        sessionProducer: ReceiveChannel<SmtpSession>,
        pool: ConnectionPool,
        smtpRunner: SmtpRunner
    ) = coroutineScope {

        sessionProducer.consumeAsFlow()
            .collect { session ->
                session.listener = listener
                pool.get()
                smtpRunner.run(session)
                pool.free()
            }
    }

    override suspend fun onComplete() {
        super.onComplete()
        smtpRunner.stop()
    }
}

