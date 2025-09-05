package br.com.mobicare.cielo.mySales.data.model


data class SaleHistory(
        val merchantId: String?,
        val paymentNode: Int?,
        val cnpjRoot: Int?,
        val date: String?,
        val saleDate: String?,
        val saleCode: String?,
        val saleAuthorizationCode: String?,
        val saleGrossAmount: Long?,
        val paymentType: String?,
        val paymentTypeCode: String?,
        val productTypeCode: String?,
        val productType: String?,
        val cardBrand: String?,
        val cardBrandCode: String?,
        val amount: Double?,
        val netAmount: Double?,
        val grossAmount: Double?,
        val nsu: String?,
        val orderNumber: String?,
        val transactionId: String?,
        val status: String?,
        val averageAmount: Double?,
        val quantity: Int?,
        val percentage: Double?
)