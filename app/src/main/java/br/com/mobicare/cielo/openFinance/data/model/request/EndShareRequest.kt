package br.com.mobicare.cielo.openFinance.data.model.request

data class EndShareRequest (
    val authorizationCode: String? = null,
    val requestId: String? = null,
    val function: String,
    val consentId: String,
    val shareId: String,
    val flow: String
)