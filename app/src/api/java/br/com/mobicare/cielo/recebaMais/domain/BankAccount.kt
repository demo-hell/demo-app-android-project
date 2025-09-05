package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BankAccount(
        @SerializedName("account")
        val account: String,
        @SerializedName("accountDigit")
        val accountDigit: String,
        @SerializedName("agency")
        val agency: String,
        @SerializedName("agencyDigit")
        val agencyDigit: String,
        @SerializedName("code")
        val code: Int,
        @SerializedName("name")
        val name: String
): Parcelable