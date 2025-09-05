package br.com.mobicare.cielo.eventTracking.data.model.response

data class Detail(
    val id: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val equipments: List<Equipment?>? = null,
    val logisticOperator: String? = null,
    val serviceForecastDate: String? = null
)