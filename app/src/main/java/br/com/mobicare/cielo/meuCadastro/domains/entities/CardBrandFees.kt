package br.com.mobicare.cielo.meuCadastro.domains.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by gustavon on 15/01/18.
 */
@Parcelize
data class CardBrandFees (var cardBrands: ArrayList<CardBrands> = ArrayList()) : Parcelable