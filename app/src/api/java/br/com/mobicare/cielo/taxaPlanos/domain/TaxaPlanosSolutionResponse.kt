package br.com.mobicare.cielo.taxaPlanos.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TaxaPlanosSolutionResponse(
        val pos: List<TaxaPlanosMachine>,
        val mobile: List<TaxaPlanosMachine>
) : Parcelable