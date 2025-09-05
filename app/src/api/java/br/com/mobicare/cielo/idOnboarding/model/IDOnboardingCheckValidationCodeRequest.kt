package br.com.mobicare.cielo.idOnboarding.model

import androidx.annotation.Keep

@Keep
data class IDOnboardingCheckValidationCodeRequest(
    val code: String? = ""
)