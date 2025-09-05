package br.com.mobicare.cielo.posVirtual.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.posVirtual.data.dataSource.remote.PosVirtualAPI
import br.com.mobicare.cielo.posVirtual.data.model.request.PosVirtualCreateQRCodeRequest
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualCreateQRCodeResponse

class PosVirtualQRCodePixDataSource(
    private val serverAPI: PosVirtualAPI,
    private val safeAPICaller: SafeApiCaller
) {

    suspend fun postPosVirtualCreateQRCodePix(
        otpCode: String,
        request: PosVirtualCreateQRCodeRequest
    ): CieloDataResult<PosVirtualCreateQRCodeResponse> {
        var result: CieloDataResult<PosVirtualCreateQRCodeResponse> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeAPICaller.safeApiCall {
            serverAPI.postCreateQRCodePix(otpCode, request)
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