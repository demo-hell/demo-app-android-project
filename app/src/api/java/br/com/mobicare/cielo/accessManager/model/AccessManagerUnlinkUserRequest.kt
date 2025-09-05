package br.com.mobicare.cielo.accessManager.model

data class AccessManagerUnlinkUserRequest(
    val userId: String,
    val reason: String?
)
