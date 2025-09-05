package br.com.mobicare.cielo.mySales.data.model.params

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class GetMerchantParams(
    val authorization: String,
    val access_token: String
): Parcelable
