package net.silve.domain

import io.netty.handler.codec.smtp.SmtpCommand
import net.silve.smtpc.client.SendStatus
import net.silve.smtpc.client.SendStatusCode
import net.silve.smtpc.listener.SmtpSessionListener

class SmtpListener(private val notifier: (state: SmtpSessionState) -> Unit) : SmtpSessionListener {
    override fun onConnect(host: String?, port: Int) {
        notifier(ConnectState)
    }

    override fun onStart(host: String?, port: Int, id: String?) {
        // do nothing
    }

    override fun onError(id: String?, throwable: Throwable?) {
        // do nothing
    }

    override fun onRequest(id: String?, command: SmtpCommand?, parameters: MutableList<CharSequence>?) {
        // do nothing
    }

    override fun onData(id: String?, size: Int, duration: Long) {
        notifier(DataState.newInstance(size, duration))
    }

    override fun onCompleted(id: String?) {
        notifier(CompletedState)
    }

    override fun onResponse(id: String?, code: Int, details: MutableList<CharSequence>?) {
        // do nothing
    }

    override fun onStartTls(id: String?) {
        // do nothing
    }

    override fun onSendStatus(id: String?, status: SendStatus?) {
        when (status?.code) {
            SendStatusCode.SENT -> notifier(SendStatusState)
            else -> notifier(NotSendStatusState)
        }
    }

    companion object Factory {
        fun create(notifier: (state: SmtpSessionState) -> Unit): SmtpListener {
            return SmtpListener(notifier)
        }
    }
}
