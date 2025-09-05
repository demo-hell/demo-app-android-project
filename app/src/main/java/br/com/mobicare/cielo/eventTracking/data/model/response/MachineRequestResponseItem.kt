package br.com.mobicare.cielo.eventTracking.data.model.response

import br.com.mobicare.cielo.eventTracking.data.mapper.MachineRequestServiceType

data class MachineRequestResponseItem(
    val createdDate: String? = null,
    val deliveryType: String? = null,
    val details: List<Detail?>? = null,
    val event: String? = null,
    val id: String? = null,
    val merchant: String? = null,
    val owner: String? = null,
    val serviceType: MachineRequestServiceType? = null,
    val type: Type? = null
)