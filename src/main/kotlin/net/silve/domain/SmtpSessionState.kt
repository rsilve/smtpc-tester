package net.silve.domain

import io.netty.util.Recycler

interface SmtpSessionState

object CompletedState : SmtpSessionState

object ConnectState : SmtpSessionState

object NotSendStatusState : SmtpSessionState
object SendStatusState : SmtpSessionState

class DataState private constructor() : SmtpSessionState {
    companion object {
        private val RECYCLER: Recycler<DataState> = object : Recycler<DataState>() {
            override fun newObject(handle: Handle<DataState>): DataState {
                return DataState(handle)
            }
        }

        fun newInstance(size: Int, duration: Long): DataState {
            val obj = RECYCLER.get()
            obj.size = size
            obj.duration = duration
            return obj
        }
    }

    private constructor(newHandle: Recycler.Handle<DataState>) : this() {
        handle = newHandle
    }
    private lateinit var handle: Recycler.Handle<DataState>
    var size: Int = 0
    var duration: Long = 0

    fun recycle() {
        size = 0
        duration = 0
        handle.recycle(this)
    }



}