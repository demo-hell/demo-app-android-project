package br.com.mobicare.cielo.pixMVVM.domain.model

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import java.time.ZonedDateTime

data class PixRefundDetail(
    val idAccount: String? = null,
    val idEndToEndOriginal: String? = null,
    val idEndToEndReturn: String? = null,
    val transactionDate: ZonedDateTime? = null,
    val transactionType: PixExtractType? = null,
    val errorType: String? = null,
    val transactionStatus: PixTransactionStatus? = null,
    val creditParty: RefundParty? = null,
    val debitParty: RefundParty? = null,
    val amount: Double? = null,
    val tariffAmount: Double? = null,
    val finalAmount: Double? = null,
    val reversalCode: String? = null,
    val reversalReason: String? = null,
    val idAdjustment: String? = null,
    val transactionCode: String? = null,
    val transactionCodeOriginal: String? = null,
    val payerAnswer: String? = null,
    val enable: PixEnable? = null,
) {
    data class RefundParty(
        val ispb: String? = null,
        val bankName: String? = null,
        val nationalRegistration: String? = null,
        val name: String? = null,
        val bankBranchNumber: String? = null,
        val bankAccountNumber: String? = null,
        val bankAccountType: String? = null,
    )
}
