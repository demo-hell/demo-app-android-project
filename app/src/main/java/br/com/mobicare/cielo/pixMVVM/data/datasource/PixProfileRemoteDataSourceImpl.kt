package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixProfileRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixProfileRemoteDataSource

class PixProfileRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixProfileRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun update(otpCode: String?, request: PixProfileRequest): CieloDataResult<String> {
        var result: CieloDataResult<String> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.putProfile(otpCode, request)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(EMPTY_VALUE)
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}