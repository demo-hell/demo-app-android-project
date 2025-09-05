package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TapOnPhoneOrderResponse(
    val orderId: String? = null
) : Parcelable
