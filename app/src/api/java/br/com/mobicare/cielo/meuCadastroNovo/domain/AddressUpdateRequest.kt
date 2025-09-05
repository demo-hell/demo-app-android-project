package br.com.mobicare.cielo.meuCadastroNovo.domain


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressUpdateRequest(
        val id: String,
        val country: String,
        val state: String,
        val description: String,
        val streetAddress2: String?,
        val city: String,
        val neighborhood: String,
        val streetAddress: String,
        val number: String,
        val zipCode: String,
        val purposeAddress: List<PurposeAddress?>
) : Parcelable

@Parcelize
data class PurposeAddress(
        val type: Int? = null
): Parcelable