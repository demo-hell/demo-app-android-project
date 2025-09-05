package br.com.mobicare.cielo.chargeback.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChargebackResponse(
    val actionTakenCode: String? = null,
    val caseId: Int?,
    val chargebackDetails: ChargebackDetailsResponse?,
    val chargebackId: Int?,
    val daysToDeadLine: Int?,
    val lifecycle: LifecycleResponse?,
    val merchantId: Long?,
    val process: String?,
    val disputeStatus: List<Int>? = null,
    val transactionAmount: Double?,
    val transactionDetails: TransactionDetailsResponse?,
    val treatmentDeadline: String?
) : Parcelable

@Keep
@Parcelize
data class ChargebackDetailsResponse(
    val descriptionReason: String?,
    val reasonCode: String?,
    val reasonDescription: String?,
    val reasonType: Int?,
    val receptionDate: String?,
    val replyDate: String?,
    val chargebackMessage: String?,
    val descriptionReasonType: String?,
    val fastDisputeResolution: Boolean?,
    val refundFileInformation: List<RefundFileInformationList>
) : Parcelable

@Keep
@Parcelize
data class RefundFileInformationList(
    val documentId: Int,
    val nameFile: String
) : Parcelable

@Keep
@Parcelize
data class LifecycleResponse(
    val action: String?,
    val actionDate: String?
) : Parcelable

@Keep
@Parcelize
data class TransactionDetailsResponse(
    val authorizationCode: String?,
    val cardBrandCode: Int?,
    val cardBrandName: String?,
    val issuerSenderCode: String?,
    val issuerSenderDescription: String?,
    val merchantName: String?,
    val nsu: Int?,
    val productType: String?,
    val productTypeCode: Int?,
    val referenceNumber: String?,
    val roNumber: String?,
    val terminal: String?,
    val transactionDate: String?,
    val truncatedCardNumber: String?,
    val tid: String?,
    val currency: String?
) : Parcelable

