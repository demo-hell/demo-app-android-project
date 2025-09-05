package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class OfferSet(
        @SerializedName("offers")
        val offers: List<Offer>
)