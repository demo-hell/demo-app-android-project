package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixProfileResponse(
    val documentNumber: String? = null,
    val events: List<Event>? = null,
    val settlementActive: Boolean? = null,
    val profileType: String? = null,
    val blockType: String? = null
) : Parcelable

@Keep
@Parcelize
data class Event(
    val dockAccountId: String? = null,
    val merchantId: String? = null,
    val status: String? = null,
    val timestampRequest: String? = null,
    val type: String? = null,
    val user: String? = null
) : Parcelable