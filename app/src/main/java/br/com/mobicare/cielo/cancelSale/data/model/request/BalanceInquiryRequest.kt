package br.com.mobicare.cielo.cancelSale.data.model.request

data class BalanceInquiryRequest(
    val cardBrandCode: String,
    val authorizationCode: String,
    val nsu: String,
    val truncatedCardNumber: String,
    val initialDate: String,
    val finalDate: String,
    val paymentType: String,
    val grossAmount: String,
    val page: Int?,
    val pageSize: Int?,
)
