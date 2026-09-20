package com.gzpu.livesdk

sealed class LiveEvent {
    data object Connecting : LiveEvent()
    data object Connected : LiveEvent()
    data object Disconnected : LiveEvent()
    data class Error(val message: String, val cause: Throwable? = null) : LiveEvent()
    data class IceState(val state: String) : LiveEvent()
}

interface LiveEventListener {
    fun onEvent(event: LiveEvent)
}
