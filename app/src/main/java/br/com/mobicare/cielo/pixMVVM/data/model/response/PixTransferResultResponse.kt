package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixTransferResultResponse(
    val endToEndId: String? = null,
    val transactionCode: String? = null,
    val transactionDate: String? = null,
    val transactionStatus: String? = null,
    val schedulingDate: String? = null,
    val schedulingCode: String? = null
)
