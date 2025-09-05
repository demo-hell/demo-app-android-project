package br.com.mobicare.cielo.autoAtendimento.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EstablishmentSelectedObj(var ec: String, var tradeName: String?, var cnpj: String?): Parcelable