package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixTransferResponse(
    val idEndToEnd: String? = null,
    val transactionCode: String? = null,
    val transactionDate: String? = null,
    val transactionStatus: String? = null,
    val schedulingDate: String? = null,
    val schedulingCode: String? = null
) : Parcelable