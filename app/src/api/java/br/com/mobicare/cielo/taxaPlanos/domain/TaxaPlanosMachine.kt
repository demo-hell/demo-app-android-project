package br.com.mobicare.cielo.taxaPlanos.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaxaPlanosMachine(
    val model: String? = null,
    val logicalNumber: String? = null,
    val logicalNumberDigit: String? = null,
    val rentalAmount: Double? = null,
    val name: String? = null,
    val description: String? = null,
    val commercialDescription: String? = null,
    val technology: String? = null,
    val replacementAllowed: Boolean? = null,
    var selectedItem: Boolean = false
) : Parcelable