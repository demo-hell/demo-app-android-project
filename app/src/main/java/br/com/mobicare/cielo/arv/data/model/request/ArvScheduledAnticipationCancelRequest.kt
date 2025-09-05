package br.com.mobicare.cielo.arv.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvScheduledAnticipationCancelRequest(
    val negotiationType: CancelNegotiationType
) : Parcelable

enum class CancelNegotiationType {
    CIELO,
    MARKET
}