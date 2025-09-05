package br.com.mobicare.cielo.p2m.data.datasource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

class P2mAcceptRemoteDataSource(
    private val serverApi: P2mApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun putP2mAccept(
        bannerId: String
    ): CieloDataResult<Void> {
        var result: CieloDataResult<Void> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.putP2mAccept(bannerId)
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