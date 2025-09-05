package br.com.mobicare.cielo.openFinance.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.openFinance.data.datasource.remote.HolderAPI

class UserCardBalanceDataSource(
    private val serverApi: HolderAPI,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getUserCardBalance(cardProxy: String): CieloDataResult<PrepaidBalanceResponse> {
        var result: CieloDataResult<PrepaidBalanceResponse> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverApi.getUserCardBalance(cardProxy)
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