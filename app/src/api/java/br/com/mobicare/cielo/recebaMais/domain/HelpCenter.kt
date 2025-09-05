package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HelpCenter(
        val id : String,
        val title : String,
        val doubts : List<Doub>) : Parcelable