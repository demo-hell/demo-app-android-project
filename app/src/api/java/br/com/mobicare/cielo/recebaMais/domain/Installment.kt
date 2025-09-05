package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Installment(
        var firstInstallmentStartDate: String?,
        @SerializedName("simulation")
        val simulation: Simulation,
        @SerializedName("token")
        val token: String,
        @SerializedName("email") var email : String? = null,
        @SerializedName("phone") var phone: Phone? = null,
        @SerializedName("bank") var bank: Bank? = null
) : Parcelable