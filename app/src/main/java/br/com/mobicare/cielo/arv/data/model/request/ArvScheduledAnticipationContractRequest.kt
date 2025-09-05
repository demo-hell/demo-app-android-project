package br.com.mobicare.cielo.arv.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvScheduledAnticipationContractRequest(
    val negotiationType: NegotiationType
) : Parcelable

enum class NegotiationType {
    CIELO,
    MARKET,
    BOTH
}