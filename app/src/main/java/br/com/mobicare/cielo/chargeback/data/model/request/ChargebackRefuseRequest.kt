package br.com.mobicare.cielo.chargeback.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChargebackRefuseRequest(
    val chargebackId: String?,
    val fileBase64: String?,
    val fileName: String?,
    val merchantId: String?,
    val reasonToRefuse: String?
) : Parcelable