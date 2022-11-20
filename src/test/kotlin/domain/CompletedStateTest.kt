package domain

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class CompletedStateTest {
    @Test
    internal fun `should exists`() {
        assertNotNull(CompletedState)
    }
}