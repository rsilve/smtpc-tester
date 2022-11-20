package net.silve.domain

data class Parameters(
    val sender: String = DEFAULT_SENDER,
    val recipient: String = DEFAULT_RECIPIENT,
    val port: Int = DEFAULT_PORT,
    val host: String = DEFAULT_HOST,
    val messagesNumber: Int = 1,
    val poolSize: Int = 1,
    val batchSize: Int = 10,
    val users: Int = 1,
)