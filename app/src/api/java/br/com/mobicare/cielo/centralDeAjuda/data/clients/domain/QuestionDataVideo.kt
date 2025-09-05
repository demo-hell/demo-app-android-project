package br.com.mobicare.cielo.centralDeAjuda.data.clients.domain


import com.google.gson.annotations.SerializedName

data class QuestionDataVideo(
    @SerializedName("code")
    val code: String,
    @SerializedName("enabled")
    val enabled: Boolean,
    @SerializedName("link")
    val link: String,
    @SerializedName("player")
    val player: String,
    @SerializedName("title")
    val title: String
)