package br.com.mobicare.cielo.posVirtual.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualCreateOrderResponse(
    val orderId: String? = null
) : Parcelable