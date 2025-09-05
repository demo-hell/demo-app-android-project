package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardBrand(val code: Int, val name: String) : Parcelable