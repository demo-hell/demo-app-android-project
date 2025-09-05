package br.com.mobicare.cielo.mdr.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class MdrRemoteDataSource(
    private val serverApi: MdrServerApi,
    private val safeApiCaller: SafeApiCaller,
    private val userPreferences: UserPreferences,
) {
    suspend fun postContractDecision(
        apiId: String,
        bannerId: Int,
        isAccepted: Boolean,
    ): CieloDataResult<Void> {
        var result: CieloDataResult<Void> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        val userDecision = if (isAccepted) ACCEPT else REJECT
        val apiResult =
            safeApiCaller.safeApiCall {
                serverApi.postContractDecision(apiId, userDecision)
            }

        apiResult.onSuccess {
            putContractDecision(bannerId)
            result = CieloDataResult.Empty(it.code())
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    private fun putContractDecision(bannerId: Int) {
        userPreferences.putProcessingOffer(bannerId)
    }

    companion object {
        const val ACCEPT = "accept"
        const val REJECT = "reject"
    }
}
