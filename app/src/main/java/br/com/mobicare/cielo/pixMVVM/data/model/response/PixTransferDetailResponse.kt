package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixTransferDetailResponse(
    val agentMode: String? = null,
    val agentWithdrawalIspb: String? = null,
    val amount: Double? = null,
    val changeAmount: Double? = null,
    val creditParty: TransferParty? = null,
    val debitParty: TransferParty? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val errorType: String? = null,
    val finalAmount: Double? = null,
    val idAccount: String? = null,
    val idAccountType: String? = null,
    val idAdjustment: String? = null,
    val idEndToEnd: String? = null,
    val merchantNumber: String? = null,
    val originChannel: String? = null,
    val payerAnswer: String? = null,
    val pixType: String? = null,
    val purchaseAmount: Double? = null,
    val tariffAmount: Double? = null,
    val transactionCode: String? = null,
    val transactionCodeOriginal: String? = null,
    val transactionDate: String? = null,
    val transactionStatus: String? = null,
    val transactionType: String? = null,
    val transferType: Int? = null,
    val transactionReversalDeadline: String? = null,
    val expiredReversal: Boolean? = false,
    val idTx: String? = null,
    val transferOrigin: String? = null,
    val credit: Credit? = null,
    val fee: Fee? = null,
    val settlement: Settlement? = null,
    val type: String? = null,
    val enable: PixEnableResponse? = null,
) {
    data class TransferParty(
        val bankAccountNumber: String? = null,
        val bankAccountType: String? = null,
        val bankBranchNumber: String? = null,
        val bankName: String? = null,
        val ispb: String? = null,
        val key: String? = null,
        val name: String? = null,
        val nationalRegistration: String? = null,
    )

    data class Credit(
        val originChannel: String? = null,
        val creditTransactionDate: String? = null,
        val creditAmount: Double? = null,
        val creditFinalAmount: Double? = null,
        val creditIdEndToEnd: String? = null,
        val creditTransactionCode: String? = null,
    )

    data class Fee(
        val feeIdEndToEnd: String? = null,
        val feeTax: Double? = null,
        val feePaymentDate: String? = null,
        val feeTransactionStatus: String? = null,
        val feeTransactionCode: String? = null,
        val feeType: String? = null,
    )

    data class Settlement(
        val settlementIdEndToEnd: String? = null,
        val settlementDate: String? = null,
        val settlementTransactionStatus: String? = null,
        val settlementTransactionCode: String? = null,
        val settlementFinalAmount: Double? = null,
    )
}
