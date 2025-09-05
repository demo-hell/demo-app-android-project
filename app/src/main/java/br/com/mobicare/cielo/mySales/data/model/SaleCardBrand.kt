package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SaleCardBrand(@SerializedName("name") val name: String?,
                         @SerializedName("value") val code: String?): Parcelable