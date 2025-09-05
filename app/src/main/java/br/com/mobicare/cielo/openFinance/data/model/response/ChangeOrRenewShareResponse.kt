package br.com.mobicare.cielo.openFinance.data.model.response

data class ChangeOrRenewShareResponse(
    val consentId: String,
    val customerFrindlyName: String,
    val expirationDateTime: String,
    val shareType: String
)
