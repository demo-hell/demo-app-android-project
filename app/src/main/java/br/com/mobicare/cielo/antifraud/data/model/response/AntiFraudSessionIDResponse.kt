package br.com.mobicare.cielo.antifraud.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class AntiFraudSessionIDResponse(
    val sessionID: String?
) : Parcelable