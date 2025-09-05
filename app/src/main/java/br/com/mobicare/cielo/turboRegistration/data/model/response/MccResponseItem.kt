package br.com.mobicare.cielo.turboRegistration.data.model.response

data class MccResponseItem(
    val code: Int,
    val description: String,
    val subMcc: List<SubMccResponse>
)