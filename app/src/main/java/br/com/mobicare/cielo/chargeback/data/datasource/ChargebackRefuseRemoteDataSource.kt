package br.com.mobicare.cielo.chargeback.data.datasource

import br.com.mobicare.cielo.chargeback.data.datasource.remote.ChargebackServerApi
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackRefuseRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackRefuseResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

class ChargebackRefuseRemoteDataSource(
    private val serverApi: ChargebackServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun putChargebackRefuse(
        otpCode: String,
        request: ChargebackRefuseRequest
    ): CieloDataResult<ChargebackRefuseResponse> {

        var result: CieloDataResult<ChargebackRefuseResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.putChargebackRefuse(otpCode, request)
        }
        apiResult.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }
}