package br.com.mobicare.cielo.posVirtual.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualCreateOrderRequest(
    val offerId: String,
    val sessionId: String,
    val payoutData: PayoutDataRequest,
    val agreements: List<AgreementRequest>,
    val itemsConfigurations: List<String>
) : Parcelable

@Keep
@Parcelize
data class AgreementRequest(
    val code: String,
    val value: String
) : Parcelable

@Keep
@Parcelize
data class PayoutDataRequest(
    val payoutMethod: String,
    val targetBankAccount: TargetBankAccountRequest
) : Parcelable

@Keep
@Parcelize
data class TargetBankAccountRequest(
    val bankNumber: String,
    val agency: String,
    val accountNumber: String,
    val accountType: String
) : Parcelable