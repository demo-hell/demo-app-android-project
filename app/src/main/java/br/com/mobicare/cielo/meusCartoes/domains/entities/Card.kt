package br.com.mobicare.cielo.meusCartoes.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Card(
    @Transient var balance: Double = ZERO_DOUBLE,
    @SerializedName("issuer") val issuer: String? = null,
    @SerializedName("proxy") val proxyNumber: String? = null,
    @SerializedName("number") val cardNumber: String? = null,
    @SerializedName("status") val cardSituation: CardSituation? = null
) : Parcelable