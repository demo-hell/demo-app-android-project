package br.com.mobicare.cielo.machine.domain

data class MachineOfferResponse (
    val title: String,
    val priorityval : Int,
    val model: String,
    val description: String,
    val imageUrl: String,
    val items : List<MachineItemOfferResponse>
)