package br.com.mobicare.cielo.technicalSupport.data.model.request

import androidx.annotation.Keep

@Keep
data class BatteryRequest(
    val equipmentId: String? = null,
    val chargeBattery: Boolean? = null,
    val phone: String? = null
)