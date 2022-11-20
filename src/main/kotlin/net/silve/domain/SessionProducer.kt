package net.silve.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import net.silve.smtpc.message.ListMessageFactory
import net.silve.smtpc.message.Message
import net.silve.smtpc.message.MessageFactory
import net.silve.smtpc.message.SmtpSession

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceMessages(messagesCount: Int = 0, generateMessage: () -> Message) =
    produce(capacity = 100) {
        var done = 0
        while (done < messagesCount) {
            send(generateMessage())
            ++done
        }
    }

@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceMessagesBatch(messages: ReceiveChannel<Message>, batchSize: Int) =
    produce(capacity = 10) {
        var count = 0
        val queue = ArrayDeque<Message>(batchSize)
        for (message in messages) {
            if (queue.size < batchSize) {
                queue.add(message)
                ++count
            }

            if (queue.size == batchSize) {
                send(queue.toList())
                queue.clear()
            }
        }
        if (queue.size > 0) {
            send(queue.toList())
            queue.clear()
        }
    }


@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceSessionFromBatch(
    batches: ReceiveChannel<List<Message>>,
    generateSession: (MessageFactory) -> SmtpSession
) =
    produce(capacity = 10) {
        for (batch in batches) {
            val factory = ListMessageFactory(batch)
            send(generateSession(factory))
        }
    }

fun CoroutineScope.produceSessions(
    messagesCount: Int,
    batchSize: Int,
    generateMessage: () -> Message,
    generateSession: (MessageFactory) -> SmtpSession
): ReceiveChannel<SmtpSession> = Unit
    .run { produceMessages(messagesCount, generateMessage) }
    .run { produceMessagesBatch(this, batchSize) }
    .run { produceSessionFromBatch(this, generateSession) }
