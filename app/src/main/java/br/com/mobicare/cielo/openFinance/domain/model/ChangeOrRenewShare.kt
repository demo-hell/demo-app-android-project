package br.com.mobicare.cielo.openFinance.domain.model

data class ChangeOrRenewShare(
    val consentId: String,
    val customerFrindlyName: String,
    val expirationDateTime: String,
    val shareType: String
)
