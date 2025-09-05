package br.com.mobicare.cielo.eventTracking.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.eventTracking.data.datasource.api.TrackEventApi
import br.com.mobicare.cielo.eventTracking.data.mapper.toCallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus

class CallsEventRemoteDataSource(
    private val serverApi: TrackEventApi,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getAllCalls(
        startDate: String?,
        endDate: String?,
        filterRequestStatus: EventRequestStatus?,
        searchQuery: String?
    ): CieloDataResult<List<CallRequest>> {

        val apiResult = safeApiCaller.safeApiCall { serverApi.getAllCalls(startDate, endDate) }

        var result: CieloDataResult<List<CallRequest>> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        try {
            apiResult.onSuccess { response ->
                var calls = response.callResponse.map { it.toCallRequest() }

                searchQuery?.let {
                    calls = calls.filter {
                        it.referCode.contains(searchQuery, ignoreCase = true) ||
                                it.description.contains(searchQuery, ignoreCase = true)
                    }
                }

                calls = calls.filter {
                    if (filterRequestStatus == EventRequestStatus.ATTENDED){
                        it.eventRequestStatus == EventRequestStatus.ATTENDED
                    } else {
                        it.eventRequestStatus != EventRequestStatus.ATTENDED
                    }
                }

                result = CieloDataResult.Success(calls)
            }.onError {
                result = CieloDataResult.APIError(it.apiException)
            }.onEmpty {
                result = CieloDataResult.Empty()
            }
        } catch (e: Exception) {
            result =
                CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        }
        return result
    }
}