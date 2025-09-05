package br.com.mobicare.cielo.centralDeAjuda.data.clients.domain


import com.google.gson.annotations.SerializedName

data class QuestionDataResponse(
        @SerializedName("answer")
        val answer: String?,
        @SerializedName("dislikes")
        val dislikes: Int,
        @SerializedName("faqId")
        val faqId: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("likes")
        val likes: Int,
        @SerializedName("priority")
        val priority: Int,
        @SerializedName("video")
        val video: QuestionDataVideo?,
        @SerializedName("question")
        val question: String?,
        @SerializedName("subcategoryId")
        val subcategoryId: String,
        val tag: String
)