package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixRefundsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundCreated
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts

class PixRefundsRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller
) : PixRefundsRemoteDataSource {

    private val apiErrorResult = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    )

    override suspend fun getReceipts(idEndToEndOriginal: String?): CieloDataResult<PixRefundReceipts> {
        var result: CieloDataResult<PixRefundReceipts> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getRefundReceipts(idEndToEndOriginal)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(it.toEntity())
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

    override suspend fun getDetail(transactionCode: String?): CieloDataResult<PixRefundDetail> {
        var result: CieloDataResult<PixRefundDetail> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getRefundDetail(transactionCode)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(it.toEntity())
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

    override suspend fun getDetailFull(transactionCode: String?): CieloDataResult<PixRefundDetailFull> {
        var result: CieloDataResult<PixRefundDetailFull> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.getRefundDetailFull(transactionCode)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(it.toEntity())
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

    override suspend fun refund(otpCode: String?, request: PixRefundCreateRequest?): CieloDataResult<PixRefundCreated> {
        var result: CieloDataResult<PixRefundCreated> = apiErrorResult

        safeApiCaller.safeApiCall {
            serviceApi.refund(otpCode, request)
        }.onSuccess { response ->
            response.body()?.let {
                result = CieloDataResult.Success(it.toEntity())
            }
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}