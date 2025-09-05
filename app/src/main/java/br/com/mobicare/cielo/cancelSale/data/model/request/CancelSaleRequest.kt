package br.com.mobicare.cielo.cancelSale.data.model.request

data class CancelSaleRequest(
    val saleAmount: Double?,
    val saleDate: String?,
    val cardBrandCode: Int?,
    val productCode: Int?,
    val authorizationCode: String?,
    val nsu: String?,
    val refundAmount: Double?,
    val refundDate: String?
)
