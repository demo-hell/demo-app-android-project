package br.com.mobicare.cielo.openFinance.domain.model

data class ConfirmShare(
    val consentId: String,
    val customerFrindlyName: String,
    val expirationDateTime: String,
    val shareType: String
)
