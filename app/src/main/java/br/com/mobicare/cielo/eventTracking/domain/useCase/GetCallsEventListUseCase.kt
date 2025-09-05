package br.com.mobicare.cielo.eventTracking.domain.useCase

import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.repository.CallsEventRepository

class GetCallsEventListUseCase(
    val repository: CallsEventRepository
) {
    suspend operator fun invoke(
        startDate: String?,
        endDate: String?,
        filterRequestStatus: EventRequestStatus?,
        searchQuery: String?
        ) = repository.getCallsEventsList(startDate, endDate, filterRequestStatus, searchQuery)
}