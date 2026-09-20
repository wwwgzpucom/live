package com.gzpu.livesdk.internal

import com.gzpu.livesdk.LiveEvent
import com.gzpu.livesdk.LiveEventListener
import com.gzpu.livesdk.LiveSessionConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class WhepPlayerSession(
    private val listener: LiveEventListener?,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val signaling = WhipWhepSignaling()

    private var peerConnection: PeerConnection? = null
    private var remoteVideoTrack: VideoTrack? = null
    private var renderer: SurfaceViewRenderer? = null
    private var resourceUrl: String? = null
    private var token: String? = null

    fun attachRenderer(view: SurfaceViewRenderer) {
        renderer = view
        view.init(WebRtcEngine.eglBase.eglBaseContext, null)
        view.setEnableHardwareScaler(true)
        view.setMirror(false)
    }

    fun start(config: LiveSessionConfig) {
        scope.launch {
            try {
                listener?.onEvent(LiveEvent.Connecting)
                token = config.token
                createPeerConnection()
                val offer = createRecvOnlyOffer(config)
                peerConnection?.setLocalDescriptionAwait(offer)
                val session = signaling.postOffer(config.endpoint, offer.description, config.token)
                resourceUrl = session.resourceUrl
                val answer = SessionDescription(SessionDescription.Type.ANSWER, session.answerSdp)
                peerConnection?.setRemoteDescriptionAwait(answer)
                listener?.onEvent(LiveEvent.Connected)
            } catch (t: Throwable) {
                listener?.onEvent(LiveEvent.Error(t.message ?: "Play failed", t))
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
        renderer?.release()
        renderer = null
    }

    private suspend fun stopInternal() {
        try {
            resourceUrl?.let { signaling.deleteResource(it, token) }
        } catch (_: Throwable) {
        }
        resourceUrl = null
        remoteVideoTrack?.removeSink(renderer)
        remoteVideoTrack = null
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

                override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
                    val track = receiver?.track() as? VideoTrack ?: return
                    remoteVideoTrack = track
                    renderer?.let { track.addSink(it) }
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
            },
        ) ?: error("Failed to create PeerConnection")
    }

    private suspend fun createRecvOnlyOffer(config: LiveSessionConfig): SessionDescription {
        val pc = peerConnection ?: error("PeerConnection missing")
        // Transceivers make direction explicit for WHEP recvonly
        if (config.audioEnabled) {
            pc.addTransceiver(
                org.webrtc.MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
                org.webrtc.RtpTransceiver.RtpTransceiverInit(
                    org.webrtc.RtpTransceiver.RtpTransceiverDirection.RECV_ONLY,
                ),
            )
        }
        if (config.videoEnabled) {
            pc.addTransceiver(
                org.webrtc.MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO,
                org.webrtc.RtpTransceiver.RtpTransceiverInit(
                    org.webrtc.RtpTransceiver.RtpTransceiverDirection.RECV_ONLY,
                ),
            )
        }

        val constraints = MediaConstraints()
        return suspendCancellableCoroutine { cont ->
            pc.createOffer(object : SdpObserver {
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
