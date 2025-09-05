package br.com.mobicare.cielo.cancelSale.data.datasource

import br.com.mobicare.cielo.cancelSale.data.datasource.remote.CancelSaleAPI
import br.com.mobicare.cielo.cancelSale.data.mapper.CancelSaleAPIMapper
import br.com.mobicare.cielo.cancelSale.data.model.request.BalanceInquiryRequest
import br.com.mobicare.cielo.cancelSale.domain.model.BalanceInquiry
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess

class BalanceInquiryDataSource(
    private val serverApi: CancelSaleAPI,
    private val safeApiCaller: SafeApiCaller
) {
    suspend fun balanceInquiry(
        balanceInquiryRequest: BalanceInquiryRequest
    ): CieloDataResult<BalanceInquiry> {
        var result: CieloDataResult<BalanceInquiry> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        safeApiCaller.safeApiCall {
            serverApi.balanceInquiry(
                balanceInquiryRequest.cardBrandCode,
                balanceInquiryRequest.authorizationCode,
                balanceInquiryRequest.nsu,
                balanceInquiryRequest.truncatedCardNumber,
                balanceInquiryRequest.initialDate,
                balanceInquiryRequest.finalDate,
                balanceInquiryRequest.paymentType,
                balanceInquiryRequest.grossAmount,
                balanceInquiryRequest.page,
                balanceInquiryRequest.pageSize
            )
        }.onSuccess { response ->
            result = CancelSaleAPIMapper.mapToBalanceInquiryResponse(response.body())?.let {
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