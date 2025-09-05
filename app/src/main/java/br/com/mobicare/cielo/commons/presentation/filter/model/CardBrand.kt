package br.com.mobicare.cielo.commons.presentation.filter.model


import com.google.gson.annotations.SerializedName

data class CardBrand(
    @SerializedName("name")
    val name: String?,
    @SerializedName("value")
    val value: String?
)