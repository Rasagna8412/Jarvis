package com.jarvis.companion

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

/**
 * JARVIS Companion App — Mutual Pairing & Security Contracts
 * Handles PIN verification, HMAC-SHA256 signature generation, timestamp replay prevention.
 */
object SecurityContract {

    fun generatePinHandshake(pin: String, timestamp: Long): String {
        val data = "$pin:$timestamp"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }

    fun signPayload(payload: String, token: String, timestamp: Long): String {
        val message = "$timestamp:$payload"
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(token.toByteArray(Charsets.UTF_8), "HmacSHA256")
        sha256Hmac.init(secretKey)
        val signedBytes = sha256Hmac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(signedBytes, Base64.NO_WRAP)
    }
}

data class DeviceTelemetry(
    val deviceId: String,
    val deviceName: String,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val networkType: String
)

data class NotificationSummary(
    val id: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)
