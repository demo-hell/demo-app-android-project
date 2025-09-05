package br.com.mobicare.cielo.antifraud.data.dataSource

import br.com.mobicare.cielo.antifraud.data.dataSource.remote.AntiFraudAPI
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

class AntiFraudDataSource(
    private val serverAPI: AntiFraudAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getSessionID(): CieloDataResult<String> {
        var result: CieloDataResult<String> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        val apiResult = safeApiCaller.safeApiCall {
            serverAPI.getSessionID()
        }

        apiResult.onSuccess { response ->
            result = (response.body()?.sessionID?.let {
                if (it.isNotEmpty()) CieloDataResult.Success(it)
                else CieloDataResult.Empty()
            } ?: CieloDataResult.Empty())
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

}