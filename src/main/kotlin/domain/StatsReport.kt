package domain

data class StatsReport(
    var connected: Int = 0,
    var sent: Int = 0,
    var notSent: Int = 0,
    var total: Int = 0,
    var maxConnected: Int = 0,
    var totalDataSize: Int = 0,
    var totalMessageDuration: Long = 0,
    var duration: Long = 0,
) {
    override fun toString(): String {
        val avgSize = "msg_size_rate=%.2fko/m".format(avgSize())
        val avgDuration = "avg_msg_duration=%.2fms/m".format(avgDuration())
        return "connected=${connected}, sent=$sent, notSent=$notSent, count=${total}, ${avgSize}, $avgDuration, duration=${durationMS()}ms"
    }

    fun avgSize() = when {
        total > 0 -> totalDataSize / (total.toDouble() * 1000)
        else -> 0.0
    }

    fun avgDuration() = when {
        total > 0 -> totalMessageDuration / (total.toDouble() * 1000000)
        else -> 0.0
    }

    fun durationMS() = duration / 1000000
}

data class FinalReport(
    val sent: Int = 0,
    val notSent: Int = 0,
    val maxConnected: Int = 0,
    val duration: Long = 0,
    val avgDataSize: Double = 0.0,
    val avgMessageDuration: Double = 0.0,
) {


    companion object {
        fun from(report: StatsReport): FinalReport {
            val (_, sent, notSent, _, maxConnected) = report
            return FinalReport(sent, notSent, maxConnected, report.durationMS(), report.avgSize(), report.avgDuration())
        }
    }

    override fun toString(): String {
        val count = sent + notSent
        val rate: Double = count.toDouble() * 1000 / duration
        val rateStr = "avg_rate=%.2fm/s".format(rate)
        val avgSize = "msg_size_rate=%.2fKo/m".format(avgDataSize)
        val avgDuration = "msg_duration_rate=%.2fms/m".format(avgMessageDuration)
        return "Summary: sent=$sent, notSent=$notSent, maxConnected=$maxConnected, duration=${duration}ms, $rateStr, $avgSize, $avgDuration"
    }
}