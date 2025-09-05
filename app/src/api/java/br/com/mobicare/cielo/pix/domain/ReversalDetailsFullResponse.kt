package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReversalDetailsFullResponse(
    val refundDetail: ReversalDetailsResponse,
    val transferDetail: TransferDetailsResponse
) : Parcelable
