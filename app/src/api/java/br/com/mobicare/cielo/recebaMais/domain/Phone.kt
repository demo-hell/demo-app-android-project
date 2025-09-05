package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Phone(
        var areaCode: String,
        var number: String,
        var type: String
) : Parcelable