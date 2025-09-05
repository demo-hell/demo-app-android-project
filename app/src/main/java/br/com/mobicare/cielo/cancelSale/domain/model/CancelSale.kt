package br.com.mobicare.cielo.cancelSale.domain.model

import java.math.BigInteger

data class CancelSale(
    val requestId: BigInteger?,
    val errorCode: String?,
    val errorMessage: String?
)
