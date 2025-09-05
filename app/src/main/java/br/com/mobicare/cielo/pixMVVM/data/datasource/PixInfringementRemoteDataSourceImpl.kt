package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixInfringementDataSource

class PixInfringementRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixInfringementDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun getInfringement(idEndToEnd: String): CieloDataResult<PixEligibilityInfringementResponse> {
        lateinit var result: CieloDataResult<PixEligibilityInfringementResponse>

        safeApiCaller.safeApiCall {
            serviceApi.getInfringement(idEndToEnd)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: apiErrorResult
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

    override suspend fun postInfringement(
        request: PixCreateNotifyInfringementRequest
    ): CieloDataResult<PixCreateNotifyInfringementResponse> {
        lateinit var result: CieloDataResult<PixCreateNotifyInfringementResponse>

        safeApiCaller.safeApiCall {
            serviceApi.postInfringement(request)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: apiErrorResult
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}