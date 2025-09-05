package br.com.mobicare.cielo.turboRegistration.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.turboRegistration.data.dataSource.remote.RegistrationServerApi
import br.com.mobicare.cielo.turboRegistration.data.model.response.EligibilityResponse

class EligibilityDataSource(
    private val serverApi: RegistrationServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getRegistrationEligibility(): CieloDataResult<EligibilityResponse> {
        var result: CieloDataResult<EligibilityResponse> =
            CieloDataResult.APIError(
                CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
            )

        safeApiCaller.safeApiCall {
            serverApi.getEligibility()
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty { return result }
        return result
    }
}