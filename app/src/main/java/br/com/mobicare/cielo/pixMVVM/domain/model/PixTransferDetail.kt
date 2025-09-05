package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixFeeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferOrigin
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import com.google.errorprone.annotations.Keep
import kotlinx.android.parcel.Parcelize
import java.time.ZonedDateTime
import java.time.LocalDateTime

@Keep
@Parcelize
data class PixTransferDetail(
    val agentMode: String? = null,
    val agentWithdrawalIspb: String? = null,
    val amount: Double? = null,
    val changeAmount: Double? = null,
    val purchaseAmount: Double? = null,
    val finalAmount: Double? = null,
    val tariffAmount: Double? = null,
    val creditParty: TransferParty? = null,
    val debitParty: TransferParty? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val errorType: String? = null,
    val idAccount: String? = null,
    val idAccountType: String? = null,
    val idAdjustment: String? = null,
    val idEndToEnd: String? = null,
    val merchantNumber: String? = null,
    val originChannel: String? = null,
    val payerAnswer: String? = null,
    val pixType: PixQrCodeOperationType? = null,
    val transactionCode: String? = null,
    val transactionCodeOriginal: String? = null,
    val transactionDate: ZonedDateTime? = null,
    val transactionStatus: PixTransactionStatus? = null,
    val transactionType: PixTransactionType? = null,
    val transferType: PixTransferType? = null,
    val transactionReversalDeadline: ZonedDateTime?  = null,
    val expiredReversal: Boolean? = null,
    val idTx: String? = null,
    val transferOrigin: PixTransferOrigin? = null,
    val credit: Credit? = null,
    val fee: Fee? = null,
    val settlement: Settlement? = null,
    val type: PixType? = null,
    val enable: PixEnable? = null
) : Parcelable {

    @Parcelize
    data class TransferParty(
        val bankAccountNumber: String? = null,
        val bankAccountType: String? = null,
        val bankBranchNumber: String? = null,
        val bankName: String? = null,
        val ispb: String? = null,
        val key: String? = null,
        val name: String? = null,
        val nationalRegistration: String? = null
    ) : Parcelable

    @Parcelize
    data class Credit(
        val originChannel: String? = null,
        val creditTransactionDate: LocalDateTime? = null,
        val creditAmount: Double? = null,
        val creditFinalAmount: Double? = null,
        val creditIdEndToEnd: String? = null,
        val creditTransactionCode: String? = null
    ) : Parcelable

    @Parcelize
    data class Fee(
        val feeIdEndToEnd: String? = null,
        val feeTax: Double? = null,
        val feePaymentDate: ZonedDateTime? = null,
        val feeTransactionStatus: PixTransactionStatus? = null,
        val feeTransactionCode: String? = null,
        val feeType: PixFeeType? = null
    ) : Parcelable

    @Parcelize
    data class Settlement(
        val settlementIdEndToEnd: String? = null,
        val settlementDate: ZonedDateTime? = null,
        val settlementTransactionStatus: PixTransactionStatus? = null,
        val settlementTransactionCode: String? = null,
        val settlementFinalAmount: Double? = null,
    ) : Parcelable

}
