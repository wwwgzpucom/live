package com.gzpu.livesdk.internal

import android.content.Context
import com.gzpu.livesdk.LiveEvent
import com.gzpu.livesdk.LiveEventListener
import com.gzpu.livesdk.LiveSessionConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class WhipPublisherSession(
    private val listener: LiveEventListener?,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val signaling = WhipWhepSignaling()

    private var peerConnection: PeerConnection? = null
    private var videoCapturer: CameraVideoCapturer? = null
    private var surfaceHelper: SurfaceTextureHelper? = null
    private var videoSource: VideoSource? = null
    private var audioSource: AudioSource? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var resourceUrl: String? = null
    private var token: String? = null
    private var preview: SurfaceViewRenderer? = null

    fun attachPreview(renderer: SurfaceViewRenderer) {
        preview = renderer
        renderer.init(WebRtcEngine.eglBase.eglBaseContext, null)
        renderer.setMirror(true)
        renderer.setEnableHardwareScaler(true)
    }

    fun start(context: Context, config: LiveSessionConfig) {
        scope.launch {
            try {
                listener?.onEvent(LiveEvent.Connecting)
                token = config.token
                createPeerConnection()
                addLocalMedia(context, config)
                val offer = createOffer(sendrecv = true)
                peerConnection?.setLocalDescriptionAwait(offer)
                val session = signaling.postOffer(config.endpoint, offer.description, config.token)
                resourceUrl = session.resourceUrl
                val answer = SessionDescription(SessionDescription.Type.ANSWER, session.answerSdp)
                peerConnection?.setRemoteDescriptionAwait(answer)
                listener?.onEvent(LiveEvent.Connected)
            } catch (t: Throwable) {
                listener?.onEvent(LiveEvent.Error(t.message ?: "Publish failed", t))
                stopInternal()
            }
        }
    }

    fun stop() {
        scope.launch { stopInternal() }
    }

    fun release() {
        stop()
        scope.cancel()
        preview?.release()
        preview = null
    }

    private suspend fun stopInternal() {
        try {
            resourceUrl?.let { signaling.deleteResource(it, token) }
        } catch (_: Throwable) {
            // ignore teardown errors
        }
        resourceUrl = null
        localVideoTrack?.removeSink(preview)
        try {
            videoCapturer?.stopCapture()
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        videoCapturer?.dispose()
        videoCapturer = null
        surfaceHelper?.dispose()
        surfaceHelper = null
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        videoSource?.dispose()
        audioSource?.dispose()
        localVideoTrack = null
        localAudioTrack = null
        videoSource = null
        audioSource = null
        peerConnection?.close()
        peerConnection?.dispose()
        peerConnection = null
        listener?.onEvent(LiveEvent.Disconnected)
    }

    private fun createPeerConnection() {
        val rtcConfig = PeerConnection.RTCConfiguration(WebRtcEngine.iceServers()).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }
        peerConnection = WebRtcEngine.factory.createPeerConnection(
            rtcConfig,
            object : PeerConnection.Observer {
                override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                    listener?.onEvent(LiveEvent.IceState(state?.name ?: "UNKNOWN"))
                }

                override fun onSignalingChange(state: PeerConnection.SignalingState?) = Unit
                override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
                override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) = Unit
                override fun onIceCandidate(candidate: IceCandidate?) = Unit
                override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) = Unit
                override fun onAddStream(stream: MediaStream?) = Unit
                override fun onRemoveStream(stream: MediaStream?) = Unit
                override fun onDataChannel(dc: DataChannel?) = Unit
                override fun onRenegotiationNeeded() = Unit
                override fun onAddTrack(
                    receiver: org.webrtc.RtpReceiver?,
                    streams: Array<out MediaStream>?,
                ) = Unit
            },
        ) ?: error("Failed to create PeerConnection")
    }

    private fun addLocalMedia(context: Context, config: LiveSessionConfig) {
        val pc = peerConnection ?: return

        if (config.audioEnabled) {
            audioSource = WebRtcEngine.factory.createAudioSource(MediaConstraints())
            localAudioTrack = WebRtcEngine.factory.createAudioTrack("audio0", audioSource)
            pc.addTrack(localAudioTrack, listOf("stream0"))
        }

        if (config.videoEnabled) {
            videoSource = WebRtcEngine.factory.createVideoSource(false)
            surfaceHelper = SurfaceTextureHelper.create("CaptureThread", WebRtcEngine.eglBase.eglBaseContext)
            videoCapturer = createCapturer(context, config.useFrontCamera)
            videoCapturer?.initialize(surfaceHelper, context, videoSource?.capturerObserver)
            videoCapturer?.startCapture(config.videoWidth, config.videoHeight, config.videoFps)

            localVideoTrack = WebRtcEngine.factory.createVideoTrack("video0", videoSource)
            localVideoTrack?.setEnabled(true)
            preview?.let { localVideoTrack?.addSink(it) }
            val sender = pc.addTrack(localVideoTrack, listOf("stream0"))
            sender?.let { applyBitrate(it, config.videoBitrateBps) }
        }
    }

    private fun applyBitrate(sender: org.webrtc.RtpSender, bitrateBps: Int) {
        val params = sender.parameters
        if (params.encodings.isNotEmpty()) {
            params.encodings[0].maxBitrateBps = bitrateBps
            params.encodings[0].minBitrateBps = bitrateBps / 3
            sender.parameters = params
        }
    }

    private fun createCapturer(context: Context, front: Boolean): CameraVideoCapturer {
        val enumerator = Camera2Enumerator(context)
        val deviceName = enumerator.deviceNames.firstOrNull {
            if (front) enumerator.isFrontFacing(it) else enumerator.isBackFacing(it)
        } ?: enumerator.deviceNames.firstOrNull()
            ?: error("No camera found")
        return enumerator.createCapturer(deviceName, null)
            ?: error("Failed to create camera capturer")
    }

    private suspend fun createOffer(sendrecv: Boolean): SessionDescription {
        val constraints = MediaConstraints().apply {
            if (sendrecv) {
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
            }
        }
        return suspendCancellableCoroutine { cont ->
            peerConnection?.createOffer(object : SdpObserver {
                override fun onCreateSuccess(sdp: SessionDescription?) {
                    if (sdp != null) cont.resume(sdp) else cont.resumeWithException(IllegalStateException("Empty offer"))
                }

                override fun onCreateFailure(error: String?) {
                    cont.resumeWithException(IllegalStateException(error ?: "createOffer failed"))
                }

                override fun onSetSuccess() = Unit
                override fun onSetFailure(error: String?) = Unit
            }, constraints)
        }
    }
}

internal suspend fun PeerConnection.setLocalDescriptionAwait(sdp: SessionDescription) =
    suspendCancellableCoroutine { cont ->
        setLocalDescription(object : SdpObserver {
            override fun onSetSuccess() = cont.resume(Unit)
            override fun onSetFailure(error: String?) =
                cont.resumeWithException(IllegalStateException(error ?: "setLocalDescription failed"))
            override fun onCreateSuccess(sdp: SessionDescription?) = Unit
            override fun onCreateFailure(error: String?) = Unit
        }, sdp)
    }

internal suspend fun PeerConnection.setRemoteDescriptionAwait(sdp: SessionDescription) =
    suspendCancellableCoroutine { cont ->
        setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() = cont.resume(Unit)
            override fun onSetFailure(error: String?) =
                cont.resumeWithException(IllegalStateException(error ?: "setRemoteDescription failed"))
            override fun onCreateSuccess(sdp: SessionDescription?) = Unit
            override fun onCreateFailure(error: String?) = Unit
        }, sdp)
    }
