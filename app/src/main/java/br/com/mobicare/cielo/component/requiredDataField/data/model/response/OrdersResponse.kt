package br.com.mobicare.cielo.component.requiredDataField.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OrdersResponse(
    val orderId: String? = null
) : Parcelable