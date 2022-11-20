package application

import net.silve.domain.FinalReport
import net.silve.domain.SmtpSessionState
import net.silve.domain.Stats
import net.silve.domain.StatsReport
import io.netty.util.Recycler
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.flow
import java.time.Duration


sealed class EventMsg

object StartEventMsg : EventMsg()
class NewEventMsg private constructor() : EventMsg() {
    companion object {
        private val DEFAULT_STATE =  object : SmtpSessionState {}
        private val RECYCLER: Recycler<NewEventMsg> = object : Recycler<NewEventMsg>() {
            override fun newObject(handle: Handle<NewEventMsg>): NewEventMsg {
                return NewEventMsg(handle)
            }
        }

        fun newInstance(state: SmtpSessionState): NewEventMsg {
            val obj = RECYCLER.get()
            obj.state = state
            return obj
        }
    }

    private constructor(newHandle: Recycler.Handle<NewEventMsg>) : this() {
        handle = newHandle
    }
    private lateinit var handle: Recycler.Handle<NewEventMsg>
    var state: SmtpSessionState = DEFAULT_STATE
    fun recycle() {
        state = DEFAULT_STATE
        handle.recycle(this)
    }



}
class PrintStatsMsg private constructor() : EventMsg() {
    companion object {
        private val RECYCLER: Recycler<PrintStatsMsg> = object : Recycler<PrintStatsMsg>() {
            override fun newObject(handle: Handle<PrintStatsMsg>): PrintStatsMsg {
                return PrintStatsMsg(handle)
            }
        }

        fun newInstance(): PrintStatsMsg {
            return RECYCLER.get()
        }
    }

    private constructor(newHandle: Recycler.Handle<PrintStatsMsg>) : this() {
        handle = newHandle
    }
    private lateinit var handle: Recycler.Handle<PrintStatsMsg>

    var response = CompletableDeferred<StatsReport>()
    fun recycle() {
        response = CompletableDeferred()
        handle.recycle(this)
    }



}

@ObsoleteCoroutinesApi
fun CoroutineScope.smtpActor() = actor<EventMsg>(capacity = 1024) {
    val stats = Stats()

    for (msg in channel) { // iterate over incoming messages
        when (msg) {
            is StartEventMsg -> stats.start()
            is NewEventMsg -> stats.updateStats(msg.state)
            is PrintStatsMsg -> stats.sendStats(msg.response)
        }
    }
}

class StatsService(private val actor: SendChannel<EventMsg>) {

    private var lastReport: StatsReport = StatsReport()

    suspend fun notifyStart() {
        actor.send(StartEventMsg)
    }
    suspend fun notify(state: SmtpSessionState) {
        actor.send(NewEventMsg.newInstance(state))
    }

    @OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun pull(messagesNumber: Int) = flow {
        val tickerChannel = ticker(Duration.ofMillis(200).toMillis())
        for (tick in tickerChannel) {
            if (actor.isClosedForSend) {
                break
            }
            val msg = PrintStatsMsg.newInstance()
            actor.send(msg)
            lastReport = msg.response.await()
            if (lastReport.total >= messagesNumber && lastReport.connected == 0) {
                actor.close()
            }
            emit(lastReport)
        }
        tickerChannel.cancel()
    }
    fun finalReport(): FinalReport {
        return FinalReport.from(lastReport)
    }

}