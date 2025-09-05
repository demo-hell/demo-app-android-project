package br.com.mobicare.cielo.mySales.data.datasource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.mySales.data.mapper.SalesMapper
import br.com.mobicare.cielo.mySales.data.model.params.GetBrandsSalesFiltersParams
import br.com.mobicare.cielo.mySales.data.model.bo.CardBrandsBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO

class MySalesFiltersRemoteDataSource(
    private val serverApi: MySalesRemoteAPI,
    private val safeApiCaller: SafeApiCaller ) {
    suspend fun getFilteredCardBrands(params: GetBrandsSalesFiltersParams): CieloDataResult<CardBrandsBO>{
        var result: CieloDataResult<CardBrandsBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )
        safeApiCaller.safeApiCall {
            serverApi.getCardBrands(
                accessToken = params.accessToken,
                authorization = params.authorization
            )
        }.onSuccess { response ->
            result = SalesMapper.mapToCardBrandsBO(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }
        return result
    }

    suspend fun getFilteredPaymentTypes(params: GetBrandsSalesFiltersParams): CieloDataResult<ResultPaymentTypesBO> {
        var result: CieloDataResult<ResultPaymentTypesBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType =  ActionErrorTypeEnum.HTTP_ERROR)
        )
        safeApiCaller.safeApiCall {
            serverApi.getPaymentTypes(
                accessToken = params.accessToken,
                authorization = params.authorization,
                initialDate = params.quickFilter?.initialDate,
                finalDate = params.quickFilter?.finalDate
            )
        }.onSuccess { response ->
            result = SalesMapper.mapToResultPaymentTypesBO(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result

        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }
        return result
    }

    suspend fun getFilteredCanceledSells(params: GetBrandsSalesFiltersParams): CieloDataResult<ResultPaymentTypesBO> {
        var result: CieloDataResult<ResultPaymentTypesBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType =  ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall {
            serverApi.filterCanceledSells(
                accessToken = params.accessToken,
                initialDate = params.quickFilter?.initialDate,
                finalDate = params.quickFilter?.finalDate
            )
        }.onSuccess { response ->
            result = SalesMapper.mapToResultPaymentTypesBO(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result

        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }
        return result
    }
}