package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixTrustedDestinationResponse(
    val bankAccountNumber: String? = null,
    val bankBranchNumber: String? = null,
    val id: String? = null,
    val institutionName: String? = null,
    val ispb: Int? = null,
    val bankCode: String? = null,
    val limits: List<Limit>? = null,
    val name: String? = null,
    val nationalRegistration: String? = null,
    val servicesGroup: String? = null,
    val solicitationDate: String? = null,
    val status: String? = null
) : Parcelable

@Keep
@Parcelize
data class Limit(
    val requestDate: String? = null,
    val requestLimit: String? = null,
    val type: String? = null,
    val value: Double? = null
) : Parcelable