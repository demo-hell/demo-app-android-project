package br.com.mobicare.cielo.cancelSale.data.datasource

import br.com.mobicare.cielo.cancelSale.data.datasource.remote.CancelSaleAPI
import br.com.mobicare.cielo.cancelSale.data.mapper.CancelSaleAPIMapper
import br.com.mobicare.cielo.cancelSale.data.model.request.CancelSaleRequest
import br.com.mobicare.cielo.cancelSale.domain.model.CancelSale
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess

class CancelSaleDataSource (
    private val serverApi: CancelSaleAPI,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun cancelSale(otpCode: String, sales: ArrayList<CancelSaleRequest>): CieloDataResult<CancelSale> {
        var result: CieloDataResult<CancelSale> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        safeApiCaller.safeApiCall {
            serverApi.sendSaleToCancel(otpCode, sales)
        }.onSuccess { response ->
            result = CancelSaleAPIMapper.mapToCancelSaleResponse(response.body())?.let {
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