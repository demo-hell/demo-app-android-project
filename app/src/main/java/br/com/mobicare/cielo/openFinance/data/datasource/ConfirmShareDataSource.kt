package br.com.mobicare.cielo.openFinance.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.openFinance.data.datasource.remote.HolderAPI
import br.com.mobicare.cielo.openFinance.data.mapper.HolderAPIMapper
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare

class ConfirmShareDataSource(
    private val serverApi: HolderAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun confirmShare(request: ConfirmShareRequest): CieloDataResult<ConfirmShare> {
        var result: CieloDataResult<ConfirmShare> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        safeApiCaller.safeApiCall {
            serverApi.confirmShare(request)
        }.onSuccess { response ->
            result = HolderAPIMapper.mapToConfirmShare(response.body())?.let {
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