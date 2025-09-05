package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixAddNewTrustedDestinationRequest(
    val bankAccountNumber: String?,
    val bankBranchNumber: String?,
    val documentNumber: String? = null,
    val institutionName: String?,
    val ispb: Int?,
    val key: String? = null,
    val keyType: String? = null,
    val limits: List<TrustedDestinationLimit>,
    val name: String?,
    val serviceGroup: String?,
    val fingerprint: String?
) : Parcelable

@Keep
@Parcelize
data class TrustedDestinationLimit(
    val type: String?,
    val value: Double?
) : Parcelable