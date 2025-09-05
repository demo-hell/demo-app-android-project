package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReversalRequest(
    @SerializedName("idEndToEndOriginal")
    val idEndToEnd: String?,
    var amount: Double = ZERO_DOUBLE,
    val reversalReason: String? = null,
    val idTx: String? = null,
    val payerAnswer: String? = null,
    val fingerprint: String? = null,
) : Parcelable

