package br.com.mobicare.cielo.chargeback.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChargebackAcceptRequest(
    val chargebacks: List<ChargebackAcceptItemRequest>?
) : Parcelable

@Keep
@Parcelize
data class ChargebackAcceptItemRequest(
    val chargebackId: String?,
    val merchantId: String?
) : Parcelable