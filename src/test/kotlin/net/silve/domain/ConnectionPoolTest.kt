package net.silve.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConnectionPoolTest {

    @Test
    fun `1 element pool`() = runTest {
        val pool = ConnectionPool(1)
        launch { pool.start() }
        pool.get()
        pool.free()
        pool.get()
        pool.cancel()
    }

    @Test
    fun `2 elements pool`() = runTest {
        val pool = ConnectionPool(2)
        launch { pool.start() }
        pool.get()
        pool.get()
        pool.cancel()
    }


}