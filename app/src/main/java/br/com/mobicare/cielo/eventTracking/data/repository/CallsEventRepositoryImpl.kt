package br.com.mobicare.cielo.eventTracking.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.eventTracking.data.datasource.CallsEventRemoteDataSource
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.repository.CallsEventRepository

class CallsEventRepositoryImpl(
    private val remoteDataSource: CallsEventRemoteDataSource
) : CallsEventRepository {

    override suspend fun getCallsEventsList(
        startDate: String?,
        endDate: String?,
        filterRequestStatus: EventRequestStatus?,
        searchQuery: String?
    ): CieloDataResult<List<CallRequest>> {
        return remoteDataSource.getAllCalls(startDate, endDate, filterRequestStatus, searchQuery)
    }
}