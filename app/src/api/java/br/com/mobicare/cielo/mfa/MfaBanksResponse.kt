package br.com.mobicare.cielo.mfa

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class MfaAccount(
    @SerializedName("bankName")
    val bankName: String? = null,
    @SerializedName("bankCode")
    val bankCode: String? = null,
    @SerializedName("agency")
    val agency: String? = null,
    @SerializedName("account")
    val account: String? = null,
    @SerializedName("accountDigit")
    val accountDigit: String? = null,
    @SerializedName("accountType")
    val accountType: String? = null,
    @SerializedName("imgSource")
    val imgSource: String? = null,
    val legalEntity: String? = null,
    var identificationNumber: String? = null,
    val fingerprint: String? = null
) : Parcelable