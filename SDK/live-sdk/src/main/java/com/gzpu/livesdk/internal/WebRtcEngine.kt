package com.gzpu.livesdk.internal

import android.content.Context
import com.gzpu.livesdk.LiveSdkConfig
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.audio.JavaAudioDeviceModule

internal object WebRtcEngine {
    lateinit var appContext: Context
        private set
    lateinit var eglBase: EglBase
        private set
    lateinit var factory: PeerConnectionFactory
        private set
    private var config: LiveSdkConfig = LiveSdkConfig()

    fun initialize(context: Context, sdkConfig: LiveSdkConfig) {
        appContext = context.applicationContext
        config = sdkConfig

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(appContext)
                .setEnableInternalTracer(false)
                .createInitializationOptions(),
        )

        eglBase = EglBase.create()

        val encoderFactory = DefaultVideoEncoderFactory(
            eglBase.eglBaseContext,
            /* enableIntelVp8Encoder = */ true,
            /* enableH264HighProfile = */ sdkConfig.preferHardwareCodec,
        )
        val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)
        val audioDeviceModule = JavaAudioDeviceModule.builder(appContext).createAudioDeviceModule()

        factory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(audioDeviceModule)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()

        audioDeviceModule.release()
    }

    fun iceServers(): List<PeerConnection.IceServer> {
        if (config.iceServers.isEmpty()) {
            return listOf(
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            )
        }
        return config.iceServers.map { server ->
            val builder = PeerConnection.IceServer.builder(server.urls)
            if (!server.username.isNullOrBlank() && !server.credential.isNullOrBlank()) {
                builder.setUsername(server.username).setPassword(server.credential)
            }
            builder.createIceServer()
        }
    }
}
