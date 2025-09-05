package br.com.mobicare.cielo.cancelSale.data.model.response

import java.math.BigInteger

data class CancelSaleResponse(
    val requestId: BigInteger?,
    val errorCode: String?,
    val errorMessage: String?
)