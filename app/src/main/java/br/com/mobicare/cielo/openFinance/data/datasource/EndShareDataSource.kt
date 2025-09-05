package br.com.mobicare.cielo.openFinance.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.openFinance.data.datasource.remote.HolderAPI
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest

class EndShareDataSource(
    private val serverApi: HolderAPI,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun endShare(
        otpCode: String,
        request: EndShareRequest
    ): CieloDataResult<Any>  {
        var result: CieloDataResult<Any> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        safeApiCaller.safeApiCall {
            serverApi.endSharing(otpCode, request)
        }.onSuccess {
            result = CieloDataResult.Success(it)
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }
        return result
    }
}