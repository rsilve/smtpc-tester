package application

import net.silve.domain.MessageGenerator
import net.silve.domain.Parameters
import net.silve.domain.produceMessages
import net.silve.infra.SmtpPoolRunner
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.silve.smtpc.message.Message

class TestPlanKeepAliveSimple(
    parameters: Parameters,
    statsService: StatsService
) : TestPlanAbstract(parameters, statsService) {

    private val smtpPoolRunner = SmtpPoolRunner()

    override suspend fun execute() = coroutineScope {

        val generateMessage: () -> Message = { MessageGenerator.get(parameters) }
        launch {
            smtpPoolRunner.start(parameters, listener)
        }
        val messages = produceMessages(messagesCount = parameters.messagesNumber, generateMessage = generateMessage)
        repeat(parameters.users) {
            launch {
                oneSender(messages, smtpPoolRunner)
            }
        }
    }

    private suspend fun oneSender(messages: ReceiveChannel<Message>, smtpPoolRunner: SmtpPoolRunner) {
        messages.consumeEach { message ->
            smtpPoolRunner.sendMessage(message)
        }
    }

    override suspend fun onComplete() {
        super.onComplete()
        smtpPoolRunner.stop()
    }
}