package br.com.mobicare.cielo.commons.data.dataSource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.remote.MenuServiceAPI
import br.com.mobicare.cielo.commons.domain.datasource.MenuRemoteDataSource
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.newLogin.domain.PosVirtualWhiteListResponse

class MenuRemoteDataSourceImpl(
    private val serviceApi: MenuServiceAPI,
    private val safeApiCaller: SafeApiCaller
) : MenuRemoteDataSource {

    private val apiErrorResult =
        CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

    override suspend fun getMenu(): CieloDataResult<AppMenuResponse> {
        lateinit var result: CieloDataResult<AppMenuResponse>

        safeApiCaller.safeApiCall {
            serviceApi.getMenu()
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: apiErrorResult
        }.onEmpty {
            result = it
        }.onError {
            result = it
        }

        return result
    }

    override suspend fun getPosVirtualWhiteList(): CieloDataResult<PosVirtualWhiteListResponse> {
        lateinit var result: CieloDataResult<PosVirtualWhiteListResponse>

        safeApiCaller.safeApiCall {
            serviceApi.getPosVirtualWhiteList()
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: apiErrorResult
        }.onEmpty {
            result = it
        }.onError {
            result = it
        }

        return result
    }

}