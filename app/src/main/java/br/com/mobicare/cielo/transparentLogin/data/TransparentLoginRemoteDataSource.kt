package br.com.mobicare.cielo.transparentLogin.data

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.transparentLogin.data.remote.TransparentLoginServerApi

class TransparentLoginRemoteDataSource(
    private val serverApi: TransparentLoginServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun login(
        request: LoginRequest,
        ignoreSessionExpired: String,
        akamaiSensorData: String?
    ): CieloDataResult<LoginResponse> {
        var result: CieloDataResult<LoginResponse> = CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.login(request, ignoreSessionExpired, akamaiSensorData)
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