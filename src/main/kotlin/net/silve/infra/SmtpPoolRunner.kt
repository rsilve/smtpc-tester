package net.silve.infra

import net.silve.domain.Parameters
import net.silve.domain.SessionGenerator
import net.silve.domain.SmtpListener
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.silve.smtpc.message.Message
import net.silve.smtpc.message.QueueMessageFactory

class SmtpPoolRunner {

    private val runner = SmtpRunner()
    private lateinit var pool: List<QueueMessageFactory>
    private var count = 0

    suspend fun start(parameters: Parameters, listener: SmtpListener) = coroutineScope {
        pool = generateSequence { QueueMessageFactory(100L, parameters.batchSize, Int.MAX_VALUE) }.take(parameters.poolSize).toList()
        pool.forEach { factory ->
            val session = SessionGenerator.get(parameters, factory)
            session.listener = listener
            launch {
                runner.run(session)
            }
        }
    }

    suspend fun sendMessage(message: Message) {
        var index = count
        while (!pool[index++ % pool.size].offer(message)) {
            //if (index > count + 10000) return
            delay(1)
        }
        ++count
    }

    suspend fun stop() {
        runner.stop()
    }
}
