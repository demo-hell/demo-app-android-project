package br.com.mobicare.cielo.merchant.domain.entity

data class AgreementHistory(
    val agreementRequestInfo: List<AgreementRequestInfo>,
    val merchantId: Int
)

data class AgreementRequestInfo(
    val accountType: Int,
    val agency: Int,
    val agencyDigit: String,
    val cdBank: Int,
    val digit: String,
    val dtRequest: String,
    val dtResolution: String,
    val messageStatus: String,
    val nuCurrentAccount: String,
    val operationType: String,
    val status: String
)