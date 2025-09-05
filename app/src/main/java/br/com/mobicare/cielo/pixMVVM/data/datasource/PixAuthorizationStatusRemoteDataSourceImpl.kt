package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAuthorizationStatusRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus

class PixAuthorizationStatusRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixAuthorizationStatusRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun getPixAuthorizationStatus(): CieloDataResult<PixAuthorizationStatus> {
        var result: CieloDataResult<PixAuthorizationStatus> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getPixAuthorizationStatus()
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