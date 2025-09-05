package br.com.mobicare.cielo.openFinance.data.model.request

data class ChangeOrRenewShareRequest(
    val authorizationCode: String,
    val requestId: String,
    val idToken: String,
    val function: String,
    val consentId: String,
    val shareId: String,
    val flow: String
)
