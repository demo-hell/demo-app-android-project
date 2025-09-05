package br.com.mobicare.cielo.minhasVendas.fragments.cancelamento

import java.io.Serializable
import java.math.BigInteger

data class ResponseBanlanceInquiry(
    val authorizationCode: String,
    val availableAmount: Double,
    val cardBrandCode: Int,
    val eligible: Boolean,
    val grossAmount: Double,
    val id: String,
    val imgCardBrand: String,
    val logicalNumber: String,
    val nsu: String,
    val paymentTypeDescription: String,
    val productCode: Int,
    val saleDate: String,
    val tid: String,
    val truncatedCardNumber: String
): Serializable

data class RequestCancelApi(
        val saleAmount: Double,
        val saleDate: String,
        val cardBrandCode: Int,
        val productCode: Int,
        val authorizationCode: String,
        val nsu: String,
        val refundAmount: Double,
        val refundDate: String
)

data class ResponseCancelVenda(
        val requestId: BigInteger,
        val errorCode: String,
        val errorMessage: String

)

