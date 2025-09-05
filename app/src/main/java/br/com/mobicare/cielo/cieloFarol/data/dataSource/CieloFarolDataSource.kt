package br.com.mobicare.cielo.cieloFarol.data.dataSource

import br.com.mobicare.cielo.cieloFarol.data.dataSource.remote.CieloFarolServerApi
import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

class CieloFarolDataSource (
        private val serverApi: CieloFarolServerApi,
        private val safeApiCaller: SafeApiCaller
) {
    suspend fun getCieloFarol(
            authorization: String,
            merchant: String?
    ): CieloDataResult<CieloFarolResponse> {
        var result: CieloDataResult<CieloFarolResponse> =
                CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverApi.getCieloFarol(authorization, merchant)
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