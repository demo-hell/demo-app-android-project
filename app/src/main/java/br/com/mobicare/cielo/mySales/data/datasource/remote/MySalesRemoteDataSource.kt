package br.com.mobicare.cielo.mySales.data.datasource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.mySales.data.mapper.SalesMapper
import br.com.mobicare.cielo.mySales.data.model.params.GetCanceledSalesParams
import br.com.mobicare.cielo.mySales.data.model.params.GetMerchantParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesHistoryParams
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.data.model.bo.SalesMerchantBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO

class MySalesRemoteDataSource(
    private val serverApi: MySalesRemoteAPI,
    private val safeApiCaller: SafeApiCaller) {

    suspend fun getSummarySales(params: GetSalesDataParams): CieloDataResult<SummarySalesBO> {
        var result: CieloDataResult<SummarySalesBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall {
            serverApi.getSummarySalesOnline(
                accessToken = params.accessToken,
                authorization = params.authorization,
                initialDate = params.quickFilter.initialDate,
                finalDate = params.quickFilter.finalDate,
                cardBrand = params.quickFilter.cardBrand,
                paymentType = params.quickFilter.paymentType,
                terminal = params.quickFilter.terminal,
                status = params.quickFilter.status,
                cardNumber = params.quickFilter.cardNumber,
                nsu = params.quickFilter.nsu,
                pageSize = params.pageSize,
                authorizationCode = params.quickFilter.authorizationCode,
                page = params.page?.toString()
            )
        }.onSuccess { response ->
            val response = response.body()
            result = SalesMapper.mapToSummarySalesBO(response)?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }
        return result
    }

    suspend fun getCanceledSales(params: GetCanceledSalesParams): CieloDataResult<CanceledSummarySalesBO> {
        var result: CieloDataResult<CanceledSummarySalesBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall {
            serverApi.getCanceledSells(
                accessToken = params.accessToken,
                initialDate = params.quickFilter.initialDate,
                finalDate = params.quickFilter.finalDate,
                page = params.page,
                pageSize = params.pageSize,
                nsu = params.quickFilter.nsu,
                saleAmount = params.quickFilter.saleGrossAmount,
                refundAmount = params.quickFilter.grossAmount,
                paymentTypes = params.quickFilter.paymentType,
                cardBrands = params.quickFilter.cardBrand,
                authorizationCode = params.quickFilter.authorizationCode,
                tid = params.quickFilter.tid
            )
        }.onSuccess { response ->
            result = SalesMapper.mapToCanceledSummarySalesBO(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }
        return result
    }

    suspend fun getSummarySalesHistory(params: GetSalesHistoryParams): CieloDataResult<ResultSummarySalesHistoryBO> {
        var result: CieloDataResult<ResultSummarySalesHistoryBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )
        safeApiCaller.safeApiCall {
            serverApi.getSummarySalesHistory(
                accessToken = params.accessToken,
                authorization = params.authorization,
                initialDate = params.quickFilter.initialDate,
                finalDate = params.quickFilter.finalDate,
                cardBrand = params.quickFilter.cardBrand,
                paymentType = params.quickFilter.paymentType,
                type = params.type
            )
        }.onSuccess { response ->
            result = SalesMapper.mapToResultSummarySalesHistoryBO(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result =  CieloDataResult.APIError(it.apiException)
        }
        return result
    }


    suspend fun getMySalesTransactions(params: GetSalesDataParams): CieloDataResult<SummarySalesBO> {
        var result: CieloDataResult<SummarySalesBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall {
            serverApi.getSummarySales(
                accessToken = params.accessToken,
                authorization = params.authorization,
                initialDate = params.quickFilter.initialDate,
                finalDate = params.quickFilter.finalDate,
                initialAmount = params.quickFilter.initialAmount,
                finalAmount = params.quickFilter.finalAmount,
                customId = params.quickFilter.customId,
                saleCode = params.quickFilter.saleCode,
                truncatedCardNumber = params.quickFilter.truncatedCardNumber,
                cardBrands = params.quickFilter.cardBrand,
                paymentTypes = params.quickFilter.paymentType,
                terminal = params.quickFilter.terminal,
                status = params.quickFilter.status,
                cardNumber = params.quickFilter.cardNumber,
                nsu = params.quickFilter.nsu,
                authorizationCode = params.quickFilter.authorizationCode,
                page = params.page?.toInt(),
                pageSize = params.pageSize,
                tid = params.quickFilter.tid,
                roNumber = params.quickFilter.softDescriptor
            )
        }.onSuccess {
            val response = it.body()
            result = SalesMapper.mapToSummarySalesBO(response)?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }
        return result
    }

    suspend fun getSaleMerchant(params: GetMerchantParams): CieloDataResult<SalesMerchantBO>{
        var result : CieloDataResult<SalesMerchantBO> = CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
        )

        safeApiCaller.safeApiCall {
            serverApi.getMerchant(
                authorization = params.authorization,
                accessToken = params.access_token
            )
        }.onSuccess {
            val responseBody = it.body()
            result = SalesMapper.mapToSaleMerchantBO(responseBody)?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }
}