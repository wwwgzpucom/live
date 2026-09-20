package com.gzpu.livesdk.play

import com.gzpu.livesdk.LiveEventListener
import com.gzpu.livesdk.LiveSessionConfig
import com.gzpu.livesdk.internal.WhepPlayerSession
import org.webrtc.SurfaceViewRenderer

/**
 * Live player using WHEP (WebRTC-HTTP Egress).
 *
 * Typical flow:
 * 1. [attachRenderer]
 * 2. [start] with your media server WHEP URL
 * 3. [stop] / [release] when leaving the page
 */
class LivePlayer internal constructor() {
    private var listener: LiveEventListener? = null
    private var session: WhepPlayerSession? = null

    fun setEventListener(listener: LiveEventListener?) {
        this.listener = listener
    }

    fun attachRenderer(renderer: SurfaceViewRenderer) {
        ensureSession().attachRenderer(renderer)
    }

    fun start(config: LiveSessionConfig) {
        ensureSession().start(config)
    }

    fun stop() {
        session?.stop()
    }

    fun release() {
        session?.release()
        session = null
    }

    private fun ensureSession(): WhepPlayerSession {
        val existing = session
        if (existing != null) return existing
        return WhepPlayerSession(listener).also { session = it }
    }
}
