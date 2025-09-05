package br.com.mobicare.cielo.meusCartoes.domains.entities

import com.google.gson.annotations.SerializedName

data class CreditCardStatement(
        @SerializedName("items") val statements: List<Statement>
)

