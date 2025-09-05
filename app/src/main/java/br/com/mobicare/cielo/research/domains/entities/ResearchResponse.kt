package br.com.mobicare.cielo.research.domains.entities

import com.google.gson.annotations.SerializedName

data class ResearchResponse(
    @SerializedName("title") val title: String,
    @SerializedName("timerInSeconds") val timerInSeconds: Long,
    @SerializedName("notNowCount") val notNowCount: Int)