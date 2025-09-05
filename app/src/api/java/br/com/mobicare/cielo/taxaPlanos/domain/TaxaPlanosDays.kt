package br.com.mobicare.cielo.taxaPlanos.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaxaPlanosDays(
        val debit: Int,
        val credit: Int,
        val installment: Int
) : Parcelable