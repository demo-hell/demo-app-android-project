package br.com.mobicare.cielo.taxaPlanos.domain

import com.google.gson.annotations.SerializedName

data class InstallmentsList(

        @SerializedName("installmentNumber")
        val installmentNumber: Int?,
        @SerializedName("productMdr")
        val productMdr: Double?,
        @SerializedName("flexibleTermMdr")
        val flexibleTermMdr: Double?,
        @SerializedName("summarizedMdr")
        val summarizedMdr: Double?
)