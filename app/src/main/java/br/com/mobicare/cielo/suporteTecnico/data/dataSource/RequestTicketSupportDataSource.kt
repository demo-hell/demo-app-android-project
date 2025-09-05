package br.com.mobicare.cielo.suporteTecnico.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.ScheduleDataResponse
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.suporteTecnico.data.dataSource.remote.NewTechnicalSupportAPI
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse

class RequestTicketSupportDataSource(
    private val serverApi: NewTechnicalSupportAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getMerchant(): CieloDataResult<UserOwnerSupportResponse> {

        var result: CieloDataResult<UserOwnerSupportResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getMerchant()
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { userOwnerSupportResponse ->
                CieloDataResult.Success(userOwnerSupportResponse)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    suspend fun merchantSolutionsEquipments(): CieloDataResult<TerminalsResponse> {

        var result: CieloDataResult<TerminalsResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.merchantSolutionsEquipments()
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { terminalsResponse ->
                CieloDataResult.Success(terminalsResponse)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    suspend fun getScheduleAvailability(): CieloDataResult<ScheduleDataResponse> {

        var result: CieloDataResult<ScheduleDataResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getScheduleAvailability()
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { scheduleDataResponse ->
                CieloDataResult.Success(scheduleDataResponse)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    suspend fun postOrdersReplacements(
        request: OpenTicket
    ): CieloDataResult<OrderReplacementResponse> {

        var result: CieloDataResult<OrderReplacementResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.postOrdersReplacements(request)
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { orderReplacementResponse ->
                CieloDataResult.Success(orderReplacementResponse)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }
}