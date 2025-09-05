package br.com.mobicare.cielo.commons.presentation.filter.model


import com.google.gson.annotations.SerializedName

data class FilterReceivableResponse(
    @SerializedName("cardBrands")
    val cardBrands: List<CardBrand?>?,
    @SerializedName("paymentTypes")
    val paymentTypes: List<PaymentType?>?,
    @SerializedName("receivableTypes")
    val receivableTypes: List<ReceivableType?>?
)