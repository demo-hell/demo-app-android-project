package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SchedulingDetailResponse(
    val idAccount: Integer? = null,
    val idEndToEnd: String? = null,
    val payeeName: String? = null,
    val payeeDocumentNumber: String? = null,
    val payeeBankName: String? = null,
    val finalAmount: Double? = null,
    val message: String? = null,
    val transactionType: String? = null,
    val schedulingCreationDate: String? = null,
    val schedulingCancellationDate: String? = null,
    val schedulingDate: String? = null,
    val schedulingCode: String? = null,
    val status: String? = null
) : Parcelable