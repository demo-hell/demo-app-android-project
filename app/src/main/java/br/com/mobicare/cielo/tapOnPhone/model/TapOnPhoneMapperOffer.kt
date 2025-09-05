package br.com.mobicare.cielo.tapOnPhone.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TapOnPhoneMapperOffer(
    val type: String?,
    val description: String?,
    val fee: Double?
) : Parcelable