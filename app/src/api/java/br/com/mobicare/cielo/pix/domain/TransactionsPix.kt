package br.com.mobicare.cielo.pix.domain

import androidx.annotation.DrawableRes

data class Transaction(
    val id: Int,
    val title: String,
    @DrawableRes val image: Int
)