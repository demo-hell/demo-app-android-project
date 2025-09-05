package br.com.mobicare.cielo.meuCadastroNovo.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserAddressResponse(
        val streetAddress: String,
        val streetAddress2: String,
        val neighborhood: String,
        val city: String,
        val number: String,
        val state: String,
        val zipCode: String,
        val types: List<String>,
        val id: String,
        val addressTypes: List<String>
): Parcelable