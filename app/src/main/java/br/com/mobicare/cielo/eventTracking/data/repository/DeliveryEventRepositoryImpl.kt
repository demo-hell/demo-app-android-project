package br.com.mobicare.cielo.eventTracking.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.eventTracking.data.datasource.RequestDeliveryEventRemoteDatasource
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest
import br.com.mobicare.cielo.eventTracking.domain.repository.DeliveryEventRepository

class DeliveryEventRepositoryImpl(
    private val requestDeliveryEventRemoteDatasource: RequestDeliveryEventRemoteDatasource
) : DeliveryEventRepository {
    override suspend fun requestDeliveryEventList(
        initialDate: String?,
        endDate: String?,
        serviceType: String?,
        filterRequestStatus: EventRequestStatus?
    ): CieloDataResult<List<MachineRequest>> = requestDeliveryEventRemoteDatasource.getDeliveryEventList(initialDate, endDate, serviceType, filterRequestStatus)
}