package br.com.mobicare.cielo.openFinance.data.model.response

import androidx.annotation.Keep

@Keep
data class ConsentResponse(
    val redirectUrl: String,
    val companyName: String,
    val companyBrand: String,
    val companyLogoUrl: String,
)
