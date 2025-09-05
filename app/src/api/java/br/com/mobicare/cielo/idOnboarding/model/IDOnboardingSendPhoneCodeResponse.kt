package br.com.mobicare.cielo.idOnboarding.model

import androidx.annotation.Keep

@Keep
data class IDOnboardingSendPhoneCodeResponse(
    val cellphoneNumber: String? = "",
    val target: String? = "",
    val expiresIn: Int? = 0,
    val message: String? = ""
)
