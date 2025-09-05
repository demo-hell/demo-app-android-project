package br.com.mobicare.cielo.mfa.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class EnrollmentBankResponse(
    @SerializedName("account")
    val account: String?,
    @SerializedName("accountDigit")
    val accountDigit: String?,
    @SerializedName("accountType")
    val accountType: String?,
    @SerializedName("agency")
    val agency: String?,
    @SerializedName("agencyDigit")
    val agencyDigit: String?,
    @SerializedName("bankCode")
    val bankCode: String?,
    @SerializedName("bankName")
    val bankName: String?,
    @SerializedName("imgSource")
    val imgSource: String?,
    @SerializedName("merchantId")
    val merchantId: String?
) : Parcelable