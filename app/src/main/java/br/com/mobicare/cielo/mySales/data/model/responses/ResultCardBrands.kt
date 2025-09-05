package br.com.mobicare.cielo.mySales.data.model.responses

import android.os.Parcelable
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResultCardBrands(val cardBrands: List<CardBrand>?) : Parcelable