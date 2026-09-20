package com.gzpu.livesdk

import android.content.Context
import com.gzpu.livesdk.internal.WebRtcEngine
import com.gzpu.livesdk.play.LivePlayer
import com.gzpu.livesdk.publish.LivePublisher

/**
 * Entry point for the Android live push/pull SDK (WHIP + WHEP).
 *
 * Call [initialize] once in Application.onCreate before creating publishers/players.
 */
object LiveSdk {
    @Volatile
    private var initialized = false

    fun initialize(context: Context, config: LiveSdkConfig = LiveSdkConfig()) {
        if (initialized) return
        synchronized(this) {
            if (initialized) return
            WebRtcEngine.initialize(context.applicationContext, config)
            initialized = true
        }
    }

    fun isInitialized(): Boolean = initialized

    fun createPublisher(): LivePublisher {
        check(initialized) { "Call LiveSdk.initialize() first" }
        return LivePublisher()
    }

    fun createPlayer(): LivePlayer {
        check(initialized) { "Call LiveSdk.initialize() first" }
        return LivePlayer()
    }
}

data class LiveSdkConfig(
    /** Prefer hardware H.264 encoder/decoder when available. */
    val preferHardwareCodec: Boolean = true,
    /** Optional STUN/TURN servers for ICE. Empty = host candidates only (LAN/SRS same network). */
    val iceServers: List<IceServer> = emptyList(),
)

data class IceServer(
    val urls: List<String>,
    val username: String? = null,
    val credential: String? = null,
)
