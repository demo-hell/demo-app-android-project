package br.com.mobicare.cielo.meusCartoes.clients.api.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Destination(
        @SerializedName("bankCode") val bankCode: String,
        @Transient var bankName: String,
        @SerializedName("accountHolderName") var accountHolderName: String,
        @SerializedName("accountType") var accountType: String,
        @SerializedName("accountHolderType") var accountHolderType: String,
        @SerializedName("accountHolderDocument") var accountHolderDocument: String,
        @SerializedName("bankBranch") var bankBranch: String,
        @Transient var bankBranchDigit: String,
        @SerializedName("bankAccount") var bankAccount: String,
        @SerializedName("bankAccountDigit") var bankAccountDigit: String,
        @Transient var transferDate: String =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
) : Parcelable