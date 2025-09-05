package br.com.mobicare.cielo.pedidos.domain


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MachineItem(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("imgCardBrand")
    val imgCardBrand: String?,
    @SerializedName("orderDate")
    val orderDate: String?,
    @SerializedName("posName")
    val posName: String?
) : Serializable