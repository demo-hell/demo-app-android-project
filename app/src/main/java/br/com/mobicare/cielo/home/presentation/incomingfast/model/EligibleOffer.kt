package br.com.mobicare.cielo.home.presentation.incomingfast.model

import com.google.gson.annotations.SerializedName

data class EligibleOffer(
        @SerializedName("eligible")
        val eligible: Boolean)