package br.com.mobicare.cielo.taxaPlanos.domain

import com.google.gson.annotations.SerializedName

data class InstallmentProductsList(

        @SerializedName("installmentsList")
        val installmentsList: List<InstallmentsList>?,
        @SerializedName("prePaid")
        val prePaid: Boolean?,
        @SerializedName("productCode")
        val productCode: Int?,
        @SerializedName("productDescription")
        val productDescription: String?
)