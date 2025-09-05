package br.com.mobicare.cielo.merchant.domain.entity

data class MerchantResponseRegisterPost(
    val bankCode: Int,
    val cardBrand: String,
    val errorType: String,
    val message: String
)