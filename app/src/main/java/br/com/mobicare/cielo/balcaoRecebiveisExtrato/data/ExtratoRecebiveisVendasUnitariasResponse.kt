package br.com.mobicare.cielo.balcaoRecebiveisExtrato.data

import com.google.gson.annotations.SerializedName

data class ExtratoRecebiveisVendasUnitariasResponse (

		@SerializedName("summary") val summary : Summary,
		@SerializedName("pagination") val pagination : Pagination,
		@SerializedName("items") val items : List<ExtratoRecebiveisVendasUnitariasItems>
)