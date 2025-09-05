package br.com.mobicare.cielo.posVirtual.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualCreateQRCodeWithDrawChangeData(
    val type: String? = null,
    val amount: Double? = null,
    val modalityAlteration: Int? = null,
    val ispb: String? = null,
    val agentType: String? = null
) : Parcelable
