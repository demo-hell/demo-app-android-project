package br.com.mobicare.cielo.openFinance.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.openFinance.data.datasource.remote.HolderAPI
import br.com.mobicare.cielo.openFinance.data.model.request.RejectConsentRequest
import br.com.mobicare.cielo.openFinance.data.model.response.ConsentResponse
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants

class RejectConsentDataSource(
    private val serverApi: HolderAPI,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun rejectConsent(rejectConsentRequest: RejectConsentRequest?): CieloDataResult<ConsentResponse> {
        var result: CieloDataResult<ConsentResponse> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverApi.putRejectConsent(OpenFinanceConstants.V3, rejectConsentRequest)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }
        return result
    }
}