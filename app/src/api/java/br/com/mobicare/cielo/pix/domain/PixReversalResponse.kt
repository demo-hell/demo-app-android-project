package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixReversalResponse(
    val idEndToEndReturn: String?,
    val idEndToEndOriginal: String?,
    val transactionDate: String?,
    val idAdjustment: Int?,
    val transactionCode: String?,
    val transactionStatus: String?,
    val idTx: String? = null
) : Parcelable