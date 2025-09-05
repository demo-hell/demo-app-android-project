package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixScheduledSettlementRemoteDataSource

class PixScheduledSettlementRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixScheduledSettlementRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun create(otpCode: String?, request: PixScheduledSettlementRequest): CieloDataResult<PixScheduledSettlementResponse> {
        var result: CieloDataResult<PixScheduledSettlementResponse> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.postScheduledSettlement(otpCode, request)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(it)
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

    override suspend fun update(otpCode: String?, request: PixScheduledSettlementRequest): CieloDataResult<PixScheduledSettlementResponse> {
        var result: CieloDataResult<PixScheduledSettlementResponse> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.putScheduledSettlement(otpCode, request)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(it)
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}