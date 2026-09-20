package com.gzpu.livesdk

/**
 * Shared session options for publish (WHIP) and play (WHEP).
 *
 * @param endpoint Full WHIP or WHEP URL from your media server
 *                 e.g. https://live.example.com/rtc/v1/whip/?app=live&stream=room1
 * @param token Optional Bearer token / stream key for Authorization header
 */
data class LiveSessionConfig(
    val endpoint: String,
    val token: String? = null,
    val videoEnabled: Boolean = true,
    val audioEnabled: Boolean = true,
    val videoWidth: Int = 720,
    val videoHeight: Int = 1280,
    val videoFps: Int = 30,
    val videoBitrateBps: Int = 1_200_000,
    val useFrontCamera: Boolean = true,
)
