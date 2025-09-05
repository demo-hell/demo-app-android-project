package br.com.mobicare.cielo.login.domain


data class TokenFCM(val merchantID: String,
                    val deviceToken: String,
                    val fcmToken: String,
                    val isActive: Boolean,
                    val platform: String = "Android"
)