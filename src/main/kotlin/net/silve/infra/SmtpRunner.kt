package net.silve.infra

import kotlinx.coroutines.CompletableDeferred
import net.silve.smtpc.SmtpClient
import net.silve.smtpc.client.SmtpClientConfig
import net.silve.smtpc.message.SmtpSession

const val useTLS: Boolean = true
const val greeting: String = "greeting.tld"
const val usePipelining: Boolean = true

class SmtpRunner {

    private val config: SmtpClientConfig =
        SmtpClientConfig().useTls(useTLS).setGreeting(greeting).usePipelining(usePipelining)
    private val client = SmtpClient(config)

    suspend fun run(session: SmtpSession) {
        val deferred = CompletableDeferred<Unit>()
        client.run(session).addListener {
            deferred.complete(Unit)
        }
        deferred.await()
    }

    suspend fun stop() {
        val deferred = CompletableDeferred<Unit>()
        client.shutdownGracefully().addListener {
            deferred.complete(Unit)
        }
        deferred.await()
    }
}
