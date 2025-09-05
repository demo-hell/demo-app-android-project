package br.com.mobicare.cielo.chargeback.data.model.response

class ChargebackRefuseResponse : ArrayList<ChargebackRefuseResponseItem>()

data class ChargebackRefuseResponseItem(
    val code: Int,
    val message: String
)