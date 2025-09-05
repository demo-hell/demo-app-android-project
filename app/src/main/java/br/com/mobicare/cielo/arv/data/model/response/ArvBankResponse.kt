package br.com.mobicare.cielo.arv.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvBankResponse(
    val code: String? = null,
    val name: String? = null,
    val agency:	String? = null,
    val agencyDigit: String? = null,
    val account: String? = null,
    val accountDigit: String? = null,
    val accountType: String? = null,
    val businessType: String? = null,
    val receiveToday: Boolean = false
) : Parcelable