package br.com.mobicare.cielo.commons.domains.entities

import com.google.gson.annotations.SerializedName

data class ApiResponseError(
        @SerializedName("errorCode")
        val errorCode: String? = null,
        @SerializedName("errorMessage")
        val errorMessage: String? = null
)