package br.com.mobicare.cielo.turboRegistration.data.model.request

data class DestinationRequest(
    val account: String,
    val accountDigit: String? = null,
    val agency: String,
    val agencyDigit: String? = AgencyDigit,
    val code: String,
    val savingsAccount: Boolean? = null,
    val operationBank: String? = null
)

const val AgencyDigit = "s"