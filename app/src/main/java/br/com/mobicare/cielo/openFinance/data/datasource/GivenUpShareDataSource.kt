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
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.GivenUpShare

class GivenUpShareDataSource(
    private val serverApi: HolderAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun givenUpShare(request: GivenUpShareRequest): CieloDataResult<GivenUpShare>{
        var result: CieloDataResult<GivenUpShare> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        safeApiCaller.safeApiCall {
            serverApi.givenUpShare(request)
        }.onSuccess { response ->
            result = HolderAPIMapper.mapToGivenUpShare(response.body())?.let {
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