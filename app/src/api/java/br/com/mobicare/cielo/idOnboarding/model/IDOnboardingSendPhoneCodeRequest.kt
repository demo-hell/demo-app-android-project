package br.com.mobicare.cielo.idOnboarding.model

import androidx.annotation.Keep

@Keep
data class IDOnboardingSendPhoneCodeRequest(
    val cellphoneNumber: String? = "",
    val target: String? = ""
)
