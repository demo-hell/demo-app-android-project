package br.com.mobicare.cielo.eventTracking.domain.useCase

import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.repository.DeliveryEventRepository

class GetDeliveryEventListUseCase(val repository: DeliveryEventRepository) {
    suspend operator fun invoke(
        initialDate: String?,
        endDate: String?,
        serviceType: String?,
        filterRequestStatus: EventRequestStatus?
    ) = repository.requestDeliveryEventList(initialDate, endDate, serviceType, filterRequestStatus)
}