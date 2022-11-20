package domain

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay


class ConnectionPool(private val size: Int) {
    private val channel = Channel<Unit>(size)
    private val channelOut = Channel<Unit>(size)

    suspend fun start() = coroutineScope {
        channelOut.run {
            for (i in 1..size) {
                send(Unit)
                delay(3)
            }
            for (msg in channel) {
                send(Unit)
            }
        }
    }

    fun free() {
        channel.trySend(Unit)
    }

    suspend fun get() {
        channelOut.receive()
    }

    fun cancel() {
        channel.cancel()
    }
}