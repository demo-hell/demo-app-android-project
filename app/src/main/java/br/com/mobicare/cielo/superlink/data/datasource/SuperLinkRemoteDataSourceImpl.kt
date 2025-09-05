package br.com.mobicare.cielo.superlink.data.datasource

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.superlink.data.datasource.remote.SuperLinkServiceApi
import br.com.mobicare.cielo.superlink.data.model.response.PaymentLinkResponse
import br.com.mobicare.cielo.superlink.domain.datasource.SuperLinkDataSource

class SuperLinkRemoteDataSourceImpl(
    private val serviceApi: SuperLinkServiceApi,
    private val safeApiCaller: SafeApiCaller
) : SuperLinkDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun isPaymentLinkActive(): CieloDataResult<PaymentLinkResponse> {
        var result: CieloDataResult<PaymentLinkResponse> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.paymentLinkActive(ONE, ONE)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = it
        }.onEmpty {
            return result
        }

        return result
    }

}