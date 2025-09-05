package br.com.mobicare.cielo.openFinance.data.model.request

data class ConfirmShareRequest(
    val authorizationCode: String,
    val requestId: String,
    val idToken: String
)
