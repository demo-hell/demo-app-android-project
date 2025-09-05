package br.com.mobicare.cielo.posVirtual.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualResponse(
    val status: String? = null,
    val merchantId: String? = null,
    val impersonateRequired: Boolean? = null,
    val products: List<PosVirtualProductResponse>? = null
) : Parcelable

@Keep
@Parcelize
data class PosVirtualProductResponse(
    val id: String? = null,
    val logicalNumber: String? = null,
    val status: String? = null
) : Parcelable