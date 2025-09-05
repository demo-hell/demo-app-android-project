package br.com.mobicare.cielo.balcaoRecebiveisExtrato.data

import com.google.gson.annotations.SerializedName

data class ExtratoRecebiveisVendasUnitariasItems (

		@SerializedName("negotiationDate") val negotiationDate : String,
		@SerializedName("originalReceivableDate") val originalReceivableDate : String,
		@SerializedName("identificationNumber") val identificationNumber : String,
		@SerializedName("acquirerCode") val acquirerCode : Int,
		@SerializedName("acquirer") val acquirer : String,
		@SerializedName("paymentMethodCode") val paymentMethodCode : Int,
		@SerializedName("paymentMethod") val paymentMethod : String,
		@SerializedName("cardBrandCode") val cardBrandCode : Int?,
		@SerializedName("cardBrand") val cardBrand : String,
		@SerializedName("effectiveFee") val effectiveFee : Double,
		@SerializedName("grossAmount") val grossAmount : Double,
		@SerializedName("discountAmount") val discountAmount : Double,
		@SerializedName("netAmount") val netAmount : Double,
		var isExpaned : Boolean = false,
		var contentList: MutableList<ExtratoRecebiveisVendasItemUseCase> = ArrayList()
)