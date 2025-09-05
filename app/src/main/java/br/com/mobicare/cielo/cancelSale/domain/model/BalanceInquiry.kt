package br.com.mobicare.cielo.cancelSale.domain.model

data class BalanceInquiry(
    val authorizationCode: String?,
    val availableAmount: Double?,
    val cardBrandCode: Int?,
    val eligible: Boolean?,
    val grossAmount: Double?,
    val id: String?,
    val imgCardBrand: String?,
    val logicalNumber: String?,
    val nsu: String?,
    val paymentTypeDescription: String?,
    val productCode: Int?,
    val saleDate: String?,
    val tid: String?,
    val truncatedCardNumber: String?
)