package br.com.mobicare.cielo.chargeback.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

class ChargebackAcceptResponse : ArrayList<ChargebackAcceptResponseItem>()

@Keep
@Parcelize
data class ChargebackAcceptResponseItem(
    val code: Int? = null,
    val message: String? = null
) : Parcelable