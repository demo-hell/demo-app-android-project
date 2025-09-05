package br.com.mobicare.cielo.meusCartoes.domains.entities

import com.google.gson.annotations.SerializedName

data class CardActivationStatusResponse(@SerializedName("active") val active: Boolean)