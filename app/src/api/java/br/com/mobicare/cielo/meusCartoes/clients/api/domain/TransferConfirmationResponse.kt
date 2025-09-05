package br.com.mobicare.cielo.meusCartoes.clients.api.domain

import com.google.gson.annotations.SerializedName

data class TransferConfirmationResponse(
        @SerializedName("description")
        val message: String,
        @SerializedName("status")
        val status: String
)