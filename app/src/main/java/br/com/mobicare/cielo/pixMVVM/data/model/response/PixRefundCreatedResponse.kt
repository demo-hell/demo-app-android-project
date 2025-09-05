package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixRefundCreatedResponse(
    val idEndToEndReturn: String?,
    val idEndToEndOriginal: String?,
    val transactionDate: String?,
    val idAdjustment: String?,
    val transactionCode: String?,
    val transactionStatus: String?,
    val idTx: String? = null
)
