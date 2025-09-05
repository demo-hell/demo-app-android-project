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
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixDecodeQRCodeRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixQRCodeRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode

class PixQRCodeRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller,
) : PixQRCodeRemoteDataSource {
    private val apiErrorResult =
        CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR),
        )

    override suspend fun postDecodeQRCode(request: PixDecodeQRCodeRequest): CieloDataResult<PixDecodeQRCode> {
        var result: CieloDataResult<PixDecodeQRCode> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.postDecodeQRCode(request)
            }.onSuccess { response ->
                response.body()?.let {
                    result = CieloDataResult.Success(it.toEntity())
                }
            }.onError {
                result = it
            }.onEmpty {
                result = it
            }

        return result
    }
}
