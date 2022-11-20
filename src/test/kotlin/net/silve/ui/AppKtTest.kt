package net.silve.ui

import application.Service
import application.TestPlanChoice
import net.silve.infra.Output
import net.silve.infra.Prompt
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class AppKtTest {

    @Test
    internal fun `should read test parameters`() {
        val output = mockk<Output>()
        justRun { output.writeln(any()) }
        val mockPrompt = mockk<Prompt>()
        every { mockPrompt.prompt(any(), any()) } returns "sender" andThen "recipient" andThen "host" andThen "1"
        every { mockPrompt.promptList(any(), any(), any(), ofType(Array<TestPlanChoice>::class)) } returns TestPlanChoice.SIMPLE
        val service = Service(mockk())
        createConfiguration(service, prompter = mockPrompt, output = output)
        verify { output.writeln("== Configure test") }

    }

}