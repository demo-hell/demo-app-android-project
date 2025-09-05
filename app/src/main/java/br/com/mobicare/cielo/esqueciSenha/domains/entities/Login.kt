package br.com.mobicare.cielo.esqueciSenha.domains.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Login(var merchant: String? = null,
                 var username: String?
) : Parcelable