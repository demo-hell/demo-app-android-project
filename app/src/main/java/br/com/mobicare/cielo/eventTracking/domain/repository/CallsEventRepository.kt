package br.com.mobicare.cielo.eventTracking.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus

interface CallsEventRepository {
    suspend fun getCallsEventsList(
        startDate: String?,
        endDate: String?,
        filterRequestStatus: EventRequestStatus?,
        searchQuery: String?
    ) : CieloDataResult<List<CallRequest>>
}