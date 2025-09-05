package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixKeysRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey

class PixKeysRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixKeysRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun getAllKeys(): CieloDataResult<PixKeysResponse> {
        var result: CieloDataResult<PixKeysResponse> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getAllKeys()
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

    override suspend fun getValidateKey(
        key: String,
        keyType: String
    ): CieloDataResult<PixValidateKey> {
        lateinit var result: CieloDataResult<PixValidateKey>

        safeApiCaller.safeApiCall {
            serviceApi.getValidateKey(key, keyType)
        }.onSuccess { response ->
            result = response.body()?.let {
                CieloDataResult.Success(it.toEntity())
            } ?: apiErrorResult
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}