package application

import net.silve.domain.ConnectionPool
import net.silve.domain.Parameters
import net.silve.domain.SmtpListener
import kotlinx.coroutines.runBlocking

abstract class TestPlanAbstract(
    protected open val parameters: Parameters,
    protected open val statsService: StatsService
) : TestPlan {
    protected val pool = ConnectionPool(parameters.poolSize)
    protected val listener = SmtpListener {
        runBlocking { statsService.notify(it) }
    }

    override suspend fun beforeStart() {
        pool.start()
    }

    override suspend fun onComplete() {
        pool.cancel()
    }
}