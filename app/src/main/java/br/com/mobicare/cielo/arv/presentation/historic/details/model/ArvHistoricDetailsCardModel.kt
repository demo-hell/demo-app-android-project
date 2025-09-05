package br.com.mobicare.cielo.arv.presentation.historic.details.model

import androidx.annotation.DrawableRes

data class ArvHistoricDetailsCardModel(
        val labelOne: String?,
        val labelTwo: String? = null,
        val valueOne: String?,
        val valueTwo: String? = null,
        @DrawableRes val iconOne: Int?,
        @DrawableRes val iconTwo: Int? = null,
)
