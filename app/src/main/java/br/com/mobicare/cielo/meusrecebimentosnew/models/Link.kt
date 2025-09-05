package br.com.mobicare.cielo.meusrecebimentosnew.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Link(val rel: String,
           val href: String) : Parcelable