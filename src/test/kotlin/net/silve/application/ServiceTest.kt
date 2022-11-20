package net.silve.application

import application.Service
import application.StatsService
import application.TestPlanChoice
import application.smtpActor
import net.silve.domain.Parameters
import net.silve.domain.StatsReport
import net.silve.infra.SmtpRunner
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

internal class ServiceTest {

    @Test
    fun `should exists`() {
        Service(statsService = mockk())
    }

    @Test
    internal fun `should create parameters`() {
        val service = Service(statsService = mockk())
        service.createParameters(
            Parameters(
                sender = "sender",
                recipient = "recipient",
                port = 25,
                host = "host",
                messagesNumber = 2,
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
    @Test
    fun `should have execute method`() = runTest {
        val actor = smtpActor()
        val statsService = StatsService(actor)
        mockkConstructor(SmtpRunner::class)
        coJustRun { anyConstructed<SmtpRunner>().run(any()) }
        coJustRun { anyConstructed<SmtpRunner>().stop() }
        val service = Service(statsService = statsService)
        service.createTestPlan(TestPlanChoice.SIMPLE)
        launch {
            service.execute()
            actor.close()
            coVerify(exactly = 1) { anyConstructed<SmtpRunner>().run(any()) }
            coVerify(exactly = 1) { anyConstructed<SmtpRunner>().stop() }
        }
    }

    @Test
    fun `should have pull method`() {
        val mockStatsService = mockk<StatsService>()
        every { mockStatsService.pull(any()) } returns arrayOf(StatsReport()).asFlow()
        val service = Service(statsService = mockStatsService)
        service.pull()
        verify { mockStatsService.pull(any()) }
    }
}