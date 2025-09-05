package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixTransferBank(
    val code: Int? = null,
    val ispb: String? = null,
    val shortName: String? = null,
    val name: String? = null
) : Parcelable {
    val codeAndName get() = "$code - $name"
}
