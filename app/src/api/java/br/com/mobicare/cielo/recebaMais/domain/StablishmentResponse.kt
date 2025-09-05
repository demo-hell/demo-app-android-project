package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import br.com.mobicare.cielo.meuCadastroNovo.domain.UserAddressResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StablishmentResponse(
        val userAddresses: List<UserAddressResponse>,
        val owners : List<Owner>,
        val contacts: List<Contact>
) : Parcelable