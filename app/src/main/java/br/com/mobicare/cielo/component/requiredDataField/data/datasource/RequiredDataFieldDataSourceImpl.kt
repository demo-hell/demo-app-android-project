package br.com.mobicare.cielo.component.requiredDataField.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.component.requiredDataField.data.datasource.remote.RequiredDataFieldServiceAPI
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse
import br.com.mobicare.cielo.component.requiredDataField.domain.datasource.RequiredDataFieldDataSource

class RequiredDataFieldDataSourceImpl(
    private val serviceApi: RequiredDataFieldServiceAPI,
    private val safeApiCaller: SafeApiCaller
) : RequiredDataFieldDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun postUpdateData(
        otpCode: String,
        data: OrdersRequest
    ): CieloDataResult<OrdersResponse> {
        lateinit var result: CieloDataResult<OrdersResponse>

        safeApiCaller.safeApiCall {
            serviceApi.postUpdateData(otpCode, data)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: apiErrorResult
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}