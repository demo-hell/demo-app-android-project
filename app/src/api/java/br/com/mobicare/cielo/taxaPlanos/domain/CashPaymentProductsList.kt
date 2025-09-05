package br.com.mobicare.cielo.taxaPlanos.domain

import com.google.gson.annotations.SerializedName

data class CashPaymentProductsList(

        @SerializedName("flexibleTermMdr")
        val flexibleTermMdr: Double?,
        @SerializedName("prePaid")
        val prePaid: Boolean?,
        @SerializedName("productCode")
        val productCode: Int?,
        @SerializedName("productDescription")
        val productDescription: String?,
        @SerializedName("productMdr")
        val productMdr: Double?,
        @SerializedName("summarizedMdr")
        val summarizedMdr: Double?
)