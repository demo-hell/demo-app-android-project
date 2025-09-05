package br.com.mobicare.cielo.forgotMyPassword.domain.model

data class ForgotMyPassword(
    val tokenExpirationInMinutes: Int? = null,
    val email: String? = null,
    val nextStep: String? = null,
    val faceIdPartner: String? = null,
    var userName: String? = null
)
