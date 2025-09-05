package br.com.mobicare.cielo.openFinance.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.openFinance.data.datasource.remote.HolderAPI
import br.com.mobicare.cielo.openFinance.data.mapper.HolderAPIMapper
import br.com.mobicare.cielo.openFinance.data.model.request.ChangeOrRenewShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ChangeOrRenewShare
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess

class ChangeOrRenewShareDataSource(
    private val serverApi: HolderAPI,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun changeOrRenewShare(request: ChangeOrRenewShareRequest): CieloDataResult<ChangeOrRenewShare> {
        var result: CieloDataResult<ChangeOrRenewShare> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        safeApiCaller.safeApiCall {
            serverApi.changeOrRenewShare(request)
        }.onSuccess { response ->
            result = HolderAPIMapper.mapToChangeOrRenewShare(response.body())?.let {
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