package br.com.mobicare.cielo.pedidos.domain


import com.google.gson.annotations.SerializedName

data class OrderMachineResponse(
    @SerializedName("items")
    val machineItems: List<MachineItem>?,
    @SerializedName("pagination")
    val pagination: OrderMachinePagination?
)