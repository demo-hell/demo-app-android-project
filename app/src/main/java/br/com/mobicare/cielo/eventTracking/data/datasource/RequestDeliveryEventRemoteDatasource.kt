package br.com.mobicare.cielo.eventTracking.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.eventTracking.data.datasource.api.TrackEventApi
import br.com.mobicare.cielo.eventTracking.data.mapper.toMachineRequest
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest

class RequestDeliveryEventRemoteDatasource(
    private val trackEventApi: TrackEventApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getDeliveryEventList(
        initialDate: String?,
        endDate: String?,
        serviceType: String?,
        filterRequestStatus: EventRequestStatus?
    ): CieloDataResult<List<MachineRequest>> {

        val apiResult = safeApiCaller.safeApiCall { trackEventApi.getDeliveryEventList(initialDate, endDate, serviceType) }

        var result: CieloDataResult<List<MachineRequest>> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        try {
            apiResult.onSuccess { response ->
                result = CieloDataResult.Success(response.map { it.toMachineRequest() }
                    .filter { if (filterRequestStatus == EventRequestStatus.ATTENDED) it.requestStatus == EventRequestStatus.ATTENDED else it.requestStatus != EventRequestStatus.ATTENDED })
            }.onError {
                result = CieloDataResult.APIError(it.apiException)
            }.onEmpty {
                result = CieloDataResult.Empty()
            }
        } catch (e: Exception) {
            result = CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        }

        return result
    }
}