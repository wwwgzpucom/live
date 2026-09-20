package com.gzpu.livesdk.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * WHIP / WHEP HTTP signaling (application/sdp).
 *
 * WHIP: RFC 9725 — POST offer SDP, receive answer SDP + Location resource URL.
 * WHEP: Internet-Draft — same HTTP pattern for egress/playback.
 */
internal class WhipWhepSignaling(
    private val client: OkHttpClient = defaultClient(),
) {
    data class Session(
        val answerSdp: String,
        val resourceUrl: String?,
    )

    suspend fun postOffer(
        endpoint: String,
        offerSdp: String,
        token: String?,
    ): Session = withContext(Dispatchers.IO) {
        val body = offerSdp.toRequestBody(SDP_MEDIA_TYPE)
        val requestBuilder = Request.Builder()
            .url(endpoint)
            .post(body)
            .header("Content-Type", "application/sdp")
            .header("Accept", "application/sdp")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            val answer = response.body?.string().orEmpty()
            if (!response.isSuccessful || answer.isBlank()) {
                throw IllegalStateException(
                    "Signaling failed HTTP ${response.code}: ${answer.ifBlank { response.message }}",
                )
            }
            val location = response.header("Location")
            val resourceUrl = resolveResourceUrl(endpoint, location)
            Session(answerSdp = answer, resourceUrl = resourceUrl)
        }
    }

    suspend fun deleteResource(resourceUrl: String, token: String?) = withContext(Dispatchers.IO) {
        val requestBuilder = Request.Builder().url(resourceUrl).delete()
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        client.newCall(requestBuilder.build()).execute().use { response ->
            // 200 / 204 / 404 are all acceptable teardown outcomes
            if (!response.isSuccessful && response.code != 404) {
                throw IllegalStateException("Delete resource failed HTTP ${response.code}")
            }
        }
    }

    private fun resolveResourceUrl(endpoint: String, location: String?): String? {
        if (location.isNullOrBlank()) return null
        return if (location.startsWith("http://") || location.startsWith("https://")) {
            location
        } else {
            val base = endpoint.substringBeforeLast("/", missingDelimiterValue = endpoint)
            val path = if (location.startsWith("/")) location else "/$location"
            // Prefer absolute path on same origin when Location is relative
            if (location.startsWith("/")) {
                val schemeEnd = endpoint.indexOf("://")
                if (schemeEnd > 0) {
                    val originEnd = endpoint.indexOf('/', schemeEnd + 3)
                    val origin = if (originEnd > 0) endpoint.substring(0, originEnd) else endpoint
                    origin + location
                } else {
                    base + path
                }
            } else {
                "$base/$location"
            }
        }
    }

    companion object {
        private val SDP_MEDIA_TYPE = "application/sdp".toMediaType()

        fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
