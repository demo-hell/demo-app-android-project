package br.com.mobicare.cielo.turboRegistration.data.model.response

import com.google.gson.annotations.SerializedName

data class OperationsResponseItem(
    val label: String? = null,
    val value: String? = null,
    @SerializedName("legalEntity")
    val isLegalEntity: Boolean? = null,
    @SerializedName("savingsAccount")
    val isSavingsAccount: Boolean? = null
)