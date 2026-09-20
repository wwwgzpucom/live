package com.gzpu.livesdk.publish

import android.content.Context
import com.gzpu.livesdk.LiveEventListener
import com.gzpu.livesdk.LiveSessionConfig
import com.gzpu.livesdk.internal.WhipPublisherSession
import org.webrtc.SurfaceViewRenderer

/**
 * Live publisher using WHIP (RFC 9725).
 *
 * Typical flow:
 * 1. [attachPreview]
 * 2. [start] with your media server WHIP URL
 * 3. [stop] / [release] when leaving the page
 */
class LivePublisher internal constructor() {
    private var listener: LiveEventListener? = null
    private var session: WhipPublisherSession? = null

    fun setEventListener(listener: LiveEventListener?) {
        this.listener = listener
    }

    fun attachPreview(renderer: SurfaceViewRenderer) {
        ensureSession().attachPreview(renderer)
    }

    fun start(context: Context, config: LiveSessionConfig) {
        ensureSession().start(context.applicationContext, config)
    }

    fun stop() {
        session?.stop()
    }

    fun release() {
        session?.release()
        session = null
    }

    private fun ensureSession(): WhipPublisherSession {
        val existing = session
        if (existing != null) return existing
        return WhipPublisherSession(listener).also { session = it }
    }
}
