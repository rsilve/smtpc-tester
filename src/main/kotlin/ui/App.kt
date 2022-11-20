package ui

import application.*
import domain.*
import infra.*
import kotlinx.coroutines.*

@OptIn(ObsoleteCoroutinesApi::class)
fun runApp() = runBlocking {
    val output = Output()
    val prompter = Prompt()
    val statsService = StatsService(smtpActor())
    val service = Service(statsService,)

    createConfiguration(service, prompter = prompter, output = output)
    launch {
        executeTest(service, output = output)
        finalReport(service, output = output)
    }
    progress(service, output = output)

    return@runBlocking
}

fun createConfiguration(service: Service, prompter: Prompt, output: Output) {
    output.writeln("== Configure test")
    val parameters = Parameters(
        sender = prompter.prompt("Sender", DEFAULT_SENDER),
        recipient = prompter.prompt("Recipient", DEFAULT_RECIPIENT),
        port = DEFAULT_PORT,
        host = prompter.prompt("host", DEFAULT_HOST),
        messagesNumber = prompter.prompt("message number", DEFAULT_MESSAGE_NUMBER.toString()).toInt(),
        poolSize = prompter.prompt("connection pool size", DEFAULT_POOL_SIZE.toString()).toInt(),
        batchSize = prompter.prompt("messages batch size", DEFAULT_BATCH_SIZE.toString()).toInt(),
        users = prompter.prompt("users", DEFAULT_USERS.toString()).toInt(),
    )
    service.createParameters(parameters)

    val plan = prompter.promptList(
        "Test plan type", 2,
        arrayOf("Simple test plan", "Concurrent test plan", "Keep Alive test"),
        TestPlanChoice.values()
    )
    service.createTestPlan(plan)

}

suspend fun executeTest(service: Service, output: Output) {
    output.writeln("== Execute test")
    service.execute()
}

suspend fun progress(service: Service, output: Output) {
    service.pull().collect {
        val size = 20
        val pattern = "%-${size}s"
        val progressRate = service.progress(it, size)
        val progress = pattern.format("#".repeat(progressRate))
        val percent = "%.2f".format(service.progressPercent(it))
        output.clearAndWrite("[${progress}:${percent}%] ")
        output.write(it)
    }

}

fun finalReport(service: Service, output: Output) {
    output.clearAndWriteln(service.finalReport())
}
