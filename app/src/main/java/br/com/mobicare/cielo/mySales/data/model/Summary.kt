package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Summary(
    val totalQuantity: Int? = null,
    val totalAmount: Double? = null,
    val totalNetAmount: Double? = null
) : Parcelable