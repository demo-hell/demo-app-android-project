package br.com.mobicare.cielo.chargeback.data.datasource

import br.com.mobicare.cielo.chargeback.data.datasource.remote.ChargebackServerApi
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackAcceptResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

class ChargebackAcceptRemoteDataSource(
    private val serverApi: ChargebackServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun putChargebackAccept(
        otpCode: String,
        request: ChargebackAcceptRequest
    ): CieloDataResult<ChargebackAcceptResponse> {
        var result: CieloDataResult<ChargebackAcceptResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.putChargebackAccept(otpCode, request)
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