package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixOnBoardingRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

class PixOnBoardingRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixOnBoardingRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun getOnBoardingFulfillment(): CieloDataResult<OnBoardingFulfillment> {
        var result: CieloDataResult<OnBoardingFulfillment> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getOnBoardingFulfillment()
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it.toEntity())
            } ?: result
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}