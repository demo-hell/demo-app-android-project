package br.com.mobicare.cielo.pedidos.domain


import com.google.gson.annotations.SerializedName

data class OrderMachinePagination(
    @SerializedName("firstPage")
    val firstPage: Boolean?,
    @SerializedName("lastPage")
    val lastPage: Boolean?,
    @SerializedName("numPages")
    val numPages: Int?,
    @SerializedName("pageNumber")
    val pageNumber: Int?,
    @SerializedName("pageSize")
    val pageSize: Int?,
    @SerializedName("totalElements")
    val totalElements: Int?
)