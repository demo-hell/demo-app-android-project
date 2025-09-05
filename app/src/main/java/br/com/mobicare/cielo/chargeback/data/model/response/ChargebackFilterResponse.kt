package br.com.mobicare.cielo.chargeback.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class ChargebackFilterResponse (
    @SerializedName("brands")
    val brandsResponse: List<CardBrandResponse>,
    @SerializedName("process")
    val processResponse: List<ChargebackProcessResponse>,
    val disputeStatus: List<ChargebackDisputeStatusResponse>
): Parcelable

@Keep
@Parcelize
data class CardBrandResponse(
    @SerializedName("value")
    val brandCode: Int,
    @SerializedName("label")
    val brandName: String
): Parcelable


@Keep
@Parcelize
data class ChargebackProcessResponse(
    @SerializedName("value")
    val chargebackProcessCode: Int,
    @SerializedName("label")
    val chargebackProcessName: String
): Parcelable

@Keep
@Parcelize
data class ChargebackDisputeStatusResponse(
    @SerializedName("value")
    val chargebackDisputeStatusCode: Int,
    @SerializedName("label")
    val chargebackDisputeStatusName: String
): Parcelable
