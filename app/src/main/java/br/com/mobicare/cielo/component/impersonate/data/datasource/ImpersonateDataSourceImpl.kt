package br.com.mobicare.cielo.component.impersonate.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.component.impersonate.data.datasource.remote.ImpersonateServiceAPI
import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.data.model.response.ImpersonateResponse
import br.com.mobicare.cielo.component.impersonate.domain.datasource.ImpersonateDataSource

class ImpersonateDataSourceImpl(
    private val serviceApi: ImpersonateServiceAPI,
    private val safeApiCaller: SafeApiCaller
) : ImpersonateDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun postImpersonate(
        ec: String,
        type: String,
        impersonateRequest: ImpersonateRequest
    ): CieloDataResult<ImpersonateResponse> {
        lateinit var result: CieloDataResult<ImpersonateResponse>

        safeApiCaller.safeApiCall {
            serviceApi.postImpersonate(ec, type, impersonateRequest)
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