package application

interface TestPlan {
    suspend fun execute()

    suspend fun beforeStart()
    suspend fun onComplete()
}

enum class TestPlanChoice {
    SIMPLE, CONCURRENT, KEEP_ALIVE
}