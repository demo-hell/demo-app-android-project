package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Partner(
        @SerializedName("merchantId")
        val merchantId: String?,
        @SerializedName("name")
        val name: String?
) : Parcelable