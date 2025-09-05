package br.com.mobicare.cielo.centralDeAjuda.data.clients.domain


import com.google.gson.annotations.SerializedName

data class QuestionReactionResponse(
    @SerializedName("message")
    val message: String
)