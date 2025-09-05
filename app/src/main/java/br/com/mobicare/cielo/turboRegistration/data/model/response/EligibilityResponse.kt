package br.com.mobicare.cielo.turboRegistration.data.model.response


data class EligibilityResponse(
    val eligible: Boolean = false,
    val legalEntity: Boolean = false
)