package br.com.mobicare.cielo.pixMVVM.domain.model

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import java.time.ZonedDateTime

data class PixRefundCreated(
    val idEndToEndReturn: String? = null,
    val idEndToEndOriginal: String? = null,
    val transactionDate: ZonedDateTime? = null,
    val idAdjustment: String? = null,
    val transactionCode: String? = null,
    val transactionStatus: PixTransactionStatus? = null,
    val idTx: String? = null
)
