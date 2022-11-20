package domain

import net.silve.smtpc.SmtpContentBuilder
import net.silve.smtpc.message.Message
import kotlin.random.Random

object MessageGenerator {
    private val contentBytesList = listOf(
        {}.javaClass.getResource("/fixture001.eml")?.readBytes(),
        {}.javaClass.getResource("/fixture005.eml")?.readBytes(),
        {}.javaClass.getResource("/fixture020.eml")?.readBytes(),
        {}.javaClass.getResource("/fixture030.eml")?.readBytes(),
        {}.javaClass.getResource("/fixture040.eml")?.readBytes()
    )


    fun get(parameters: Parameters): Message {
        val index: Int = Random.nextInt(0, contentBytesList.size)
        return Message()
            .setSender(parameters.sender)
            .setRecipients(arrayOf(parameters.recipient))
            .setChunks(SmtpContentBuilder.chunks(contentBytesList[index]).iterator())
    }
}