package br.com.mobicare.cielo.arv.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvConfirmAnticipationResponse(
    val negotiationDate: String? = null,
    val operationNumber: String? = null,
    val negotiationType: String? = null,
    val status: String? = null,
    val grossAmount: Double? = null,
    val discountAmount: Double? = null,
    val netAmount: Double? = null,
    val negotiationFee: Double? = null,
    val modality: String? = null,
) : Parcelable