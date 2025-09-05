package br.com.mobicare.cielo.mfa.model

data class OtpToken(val token: String,
                    val epochCreatedAt: Long,
                    val lastUsed: Long,
                    val expiryTime: Long)