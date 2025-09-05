package br.com.mobicare.cielo.eventTracking.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest

interface DeliveryEventRepository {
    suspend fun requestDeliveryEventList(
        initialDate: String?,
        endDate: String?,
        serviceType: String?,
        filterRequestStatus: EventRequestStatus?
    ): CieloDataResult<List<MachineRequest>>
}