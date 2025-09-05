package br.com.mobicare.cielo.turboRegistration.data.model.response

data class MccResponse(
    val idAddresses: String? = null,
    val mcc: List<MccResponseItem>? = null
)