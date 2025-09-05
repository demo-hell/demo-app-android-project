package br.com.mobicare.cielo.arv.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvConfirmAnticipationRequest(
    val token: String,
    val code: String,
    val agency: String,
    val account: String,
    val accountDigit: String
) : Parcelable