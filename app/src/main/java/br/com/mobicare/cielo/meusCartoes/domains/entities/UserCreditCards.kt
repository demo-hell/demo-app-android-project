package br.com.mobicare.cielo.meusCartoes.domains.entities

import com.google.gson.annotations.SerializedName

data class UserCreditCards(
    @SerializedName("cards") val cards: List<Card>
)