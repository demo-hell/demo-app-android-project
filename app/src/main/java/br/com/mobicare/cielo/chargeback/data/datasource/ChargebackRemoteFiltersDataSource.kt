package br.com.mobicare.cielo.chargeback.data.datasource

import br.com.mobicare.cielo.chargeback.data.datasource.remote.ChargebackServerApi
import br.com.mobicare.cielo.chargeback.data.mapper.MapperChargeback
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilters
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException

class ChargebackRemoteFiltersDataSource(
    private val serverApi: ChargebackServerApi,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getChargebackFilters():  CieloDataResult<ChargebackFilters> {
        var result: CieloDataResult<ChargebackFilters> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall {
            serverApi.getChargebackFilters()
        }.onSuccess { response ->
            result = MapperChargeback.mapToChargebackFilter(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result

        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = it
        }
        return result
    }
}