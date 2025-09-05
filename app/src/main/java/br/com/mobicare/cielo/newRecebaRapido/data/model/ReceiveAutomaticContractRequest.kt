package br.com.mobicare.cielo.newRecebaRapido.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReceiveAutomaticContractRequest(
    val settlementTerm: Int?,
    val dayOfTheWeek: String?,
    val customFastRepayPeriodicity: String,
    val customFastRepayContractType: String
) : Parcelable