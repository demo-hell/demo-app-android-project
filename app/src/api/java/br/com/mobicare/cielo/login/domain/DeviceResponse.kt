package br.com.mobicare.cielo.login.domain

import java.util.*

data class DeviceResponse(
        var merchantID: String?,
        var deviceToken: String?,
        var fcmToken: String?,
        var isActive: Boolean = false,
        var platform: String?,
        var created_at: Date?
)