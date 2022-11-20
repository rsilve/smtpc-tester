package net.silve.domain

import kotlinx.coroutines.CompletableDeferred

data class Stats(
    var connected: Int = 0,
    var sent: Int = 0,
    var notSent: Int = 0,
    var maxConnected: Int = 0,
    var dataSize: Int = 0,
    var totalMessageDuration: Long = 0,

) {

    private val report: StatsReport = StatsReport()
    private var startTime: Long = 0

    init {
        updateReport()
    }

    fun updateStats(state: SmtpSessionState) {
        when (state) {
            is ConnectState -> {
                connected += 1
                maxConnected = maxConnected.coerceAtLeast(connected + 1)
            }

            is CompletedState -> connected -= 1
            is DataState -> {
                dataSize += state.size
                totalMessageDuration += state.duration
            }

            is SendStatusState -> sent += 1
            is NotSendStatusState -> notSent += 1
            else -> Unit
        }
        updateReport()
    }

    private fun updateReport() {
        report.let {
            it.connected = connected
            it.sent = sent
            it.notSent = notSent
            it.maxConnected = maxConnected
            it.total = sent + notSent
            it.totalDataSize = dataSize
            it.totalMessageDuration = totalMessageDuration
            if (startTime > 0)
                it.duration = System.nanoTime() - startTime
        }
    }

    fun sendStats(response: CompletableDeferred<StatsReport>) {
        response.complete(report)
    }

    fun start() {
        startTime = System.nanoTime()
    }


}
