package br.com.mobicare.cielo.esqueciSenha.domains.entities

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pid(var merchantId: String,
               @SerializedName("bankAccount") var bank: Bank? = null,
               var cardProxy: String? = null,
) : Parcelable
