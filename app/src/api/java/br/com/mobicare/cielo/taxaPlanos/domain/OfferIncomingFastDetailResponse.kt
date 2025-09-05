package br.com.mobicare.cielo.taxaPlanos.domain

import com.google.gson.annotations.SerializedName

data class OfferIncomingFastDetailResponse(

        @SerializedName("cashPaymentFactorOne")
		val cashPaymentFactorOne: Double?,
		@SerializedName("cashPaymentProductsList")
        val cashPaymentProductsList: List<CashPaymentProductsList>?,
		@SerializedName("category")
        val category: String?,
		@SerializedName("formattedSettlementTerm")
        val formattedSettlementTerm: String?,
		@SerializedName("installmentProductFactorOne")
        val installmentProductFactorOne: Double?,
		@SerializedName("installmentProductFactorTwo")
        val installmentProductFactorTwo: Double?,
		@SerializedName("installmentProductsList")
        val installmentProductsList: List<InstallmentProductsList>?,
		@SerializedName("olderValidStart")
        val olderValidStart: String?,
		@SerializedName("periodicityDescription")
        val periodicityDescription: String?
)