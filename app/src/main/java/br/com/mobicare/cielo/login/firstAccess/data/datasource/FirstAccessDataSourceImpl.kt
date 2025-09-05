package br.com.mobicare.cielo.login.firstAccess.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.login.firstAccess.data.datasource.remote.FirstAccessServerApi
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest

class FirstAccessDataSourceImpl(
    private val firstAccessServerApi: FirstAccessServerApi,
    private val safeApiCaller: SafeApiCaller
) {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    suspend fun registrationAccount(
        accountRegistrationPayLoadRequest: FirstAccessRegistrationRequest,
        inviteToken: String?,
        akamaiSensorData: String?
    ): CieloDataResult<FirstAccessResponse> {
        lateinit var result: CieloDataResult<FirstAccessResponse>

        safeApiCaller.safeApiCall {
            firstAccessServerApi.registrationAccount(
            accountRegistrationPayLoadRequest = accountRegistrationPayLoadRequest,
            inviteToken = inviteToken,
            akamaiSensorData = akamaiSensorData)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: apiErrorResult
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }
}