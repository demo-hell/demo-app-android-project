package br.com.mobicare.cielo.centralDeAjuda.data.clients.domains

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubCategorie(
        val id: String,
        val name: String
) : Parcelable