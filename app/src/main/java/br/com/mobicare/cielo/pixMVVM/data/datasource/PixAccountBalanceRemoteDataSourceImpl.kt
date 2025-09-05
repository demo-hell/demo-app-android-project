package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAccountBalanceRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAccountBalance

class PixAccountBalanceRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixAccountBalanceRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun getAccountBalance(): CieloDataResult<PixAccountBalance> {
        var result: CieloDataResult<PixAccountBalance> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getPixAccountBalance()
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it.toEntity())
            } ?: result
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}