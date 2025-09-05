package br.com.mobicare.cielo.eventTracking.data.model.response

data class Equipment(
    val commercialDescription: String? = null,
    val logicalNumber: String? = null,
    val modality: String? = null,
    val modelDescription: String? = null,
    val status: String? = null,
    val reason: String? = null,
    val technology: String? = null,
    val terminalDescription: String? = null,
    val terminalType: String? = null
)