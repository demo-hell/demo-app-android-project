package br.com.mobicare.cielo.idOnboarding.model

import androidx.annotation.Keep

@Keep
data class IDOnboardingSendEmailCodeResponse(
    val email: String? = "",
    val expiresIn: Int? = 0,
    val message: String? = ""
)
