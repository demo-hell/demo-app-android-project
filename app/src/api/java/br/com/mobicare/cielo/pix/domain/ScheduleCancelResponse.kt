package br.com.mobicare.cielo.pix.domain

data class ScheduleCancelResponse(
    val endToEndId: String?,
    val schedulingCode: String?,
    val schedulingDate: String?,
    val transactionCode: String?,
    val transactionDate: String?,
    val transactionStatus: String?
)