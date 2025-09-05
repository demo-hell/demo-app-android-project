package br.com.mobicare.cielo.openFinance.data.model.response

data class ConfirmShareResponse(
    val consentId: String,
    val customerFrindlyName: String,
    val expirationDateTime: String,
    val shareType: String
)
