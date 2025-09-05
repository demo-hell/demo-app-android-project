package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixRefundDetailResponse(
    val idAccount: String?,
    val idEndToEndOriginal: String?,
    val idEndToEndReturn: String?,
    val transactionDate: String?,
    val transactionType: String?,
    val errorType: String?,
    val transactionStatus: String?,
    val creditParty: RefundParty?,
    val debitParty: RefundParty?,
    val amount: Double?,
    val tariffAmount: Double?,
    val finalAmount: Double?,
    val reversalCode: String?,
    val reversalReason: String?,
    val idAdjustment: String?,
    val transactionCode: String?,
    val transactionCodeOriginal: String?,
    val payerAnswer: String?,
    val enable: PixEnableResponse? = null,
) {
    data class RefundParty(
        val ispb: String?,
        val bankName: String?,
        val nationalRegistration: String?,
        val name: String?,
        val bankBranchNumber: String?,
        val bankAccountNumber: String?,
        val bankAccountType: String?,
    )
}
