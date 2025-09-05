package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doub (val title: String,
                 val subtitle: String,
                 val img_url: String)  : Parcelable