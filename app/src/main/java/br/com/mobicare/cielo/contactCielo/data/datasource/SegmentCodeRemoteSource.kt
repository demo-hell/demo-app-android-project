package br.com.mobicare.cielo.contactCielo.data.datasource


import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.contactCielo.data.datasource.remote.SegmentCodeServerApi

class SegmentCodeRemoteSource(
    private val segmentCodeServerApi: SegmentCodeServerApi,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun getSegmentCode(): CieloDataResult<String> {
        var result: CieloDataResult<String> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            segmentCodeServerApi.getSegmentCode()
        }.onSuccess { response ->
            result = response.body()?.segmentCode?.let { CieloDataResult.Success(it) } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            return result
        }
        return result
    }
}