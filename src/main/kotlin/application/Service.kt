package application

import domain.FinalReport
import domain.Parameters
import domain.StatsReport
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Service(val statsService: StatsService) {

    private var parameters: Parameters = Parameters()
    private lateinit var testPlan: TestPlan

    fun createParameters(parameters: Parameters) {
        this.parameters = parameters
    }

    suspend fun execute() = coroutineScope {
        statsService.notifyStart()
        launch { testPlan.beforeStart() }
        testPlan.execute()
        testPlan.onComplete()
    }

    fun pull(): Flow<StatsReport> {
        return statsService.pull(parameters.messagesNumber)
    }

    fun progress(statsReport: StatsReport, size: Int): Int {
        return statsReport.total * size / parameters.messagesNumber
    }

    fun progressPercent(statsReport: StatsReport): Float {
        return statsReport.total.toFloat() * 100 / parameters.messagesNumber
    }

    fun finalReport(): FinalReport {
        return statsService.finalReport()
    }

    fun createTestPlan(plan: TestPlanChoice) {
        testPlan = when (plan) {
            TestPlanChoice.SIMPLE -> TestPlanSimple(parameters, statsService)
            TestPlanChoice.CONCURRENT -> TestPlanConcurrent(parameters, statsService)
            TestPlanChoice.KEEP_ALIVE -> TestPlanKeepAliveSimple(parameters, statsService)
        }
    }
}